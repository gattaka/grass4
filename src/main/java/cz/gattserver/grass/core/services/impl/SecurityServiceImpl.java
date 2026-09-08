package cz.gattserver.grass.core.services.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.core.model.domain.User;
import cz.gattserver.grass.core.model.repositories.UserRepository;

@Transactional
@Service("securityServiceImpl")
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RememberMeServices rememberMeServices;
    private final SecurityContextRepository securityContextRepository;

    @Value("${explicit.access.salt}")
    private String explicitAccessSalt;

    public SecurityServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager,
                               RememberMeServices rememberMeServices,
                               SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.rememberMeServices = rememberMeServices;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public String computeAccessHash(String value)  {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(explicitAccessSalt.getBytes(StandardCharsets.UTF_8));
        byte[] hash = md.digest(value.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) new SecurityContextLogoutHandler().logout(request, response, auth);
        try {
            request.logout();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
                             HttpServletResponse response) {

        UserInfoTO principal = new UserInfoTO();
        principal.setName(username);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, password);
        token.setDetails(new WebAuthenticationDetails(request));

        try {
            Authentication auth = authenticationManager.authenticate(token);
            if (auth.isAuthenticated()) {
                principal = (UserInfoTO) auth.getPrincipal();
                // https://docs.spring.io/spring-security/reference/migration/servlet/session-management.html
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(auth);
                securityContextRepository.saveContext(securityContext, request, response);
                if (remember && rememberMeServices instanceof TokenBasedRememberMeServices) {
                    TokenBasedRememberMeServices rms = (TokenBasedRememberMeServices) rememberMeServices;
                    rms.onLoginSuccess(request, response, auth);
                }
                // zapiš údaj o posledním přihlášení
                User user = userRepository.findById(principal.getId()).orElse(null);
                user.setLastLoginDate(LocalDateTime.now());
                userRepository.save(user);
            }
        } catch (BadCredentialsException e) {
            return LoginResult.FAILED_CREDENTIALS;
        } catch (DisabledException e) {
            return LoginResult.FAILED_DISABLED;
        } catch (LockedException e) {
            return LoginResult.FAILED_LOCKED;
        }
        return LoginResult.SUCCESS;
    }

    @Override
    public UserInfoTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserInfoTO) {
                return (UserInfoTO) principal;
            }
        }
        return new UserInfoTO();
    }

}
