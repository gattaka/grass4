package cz.gattserver.grass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

public class SecurityConfig {

    @Value("${remember.me.key}")
    private String REMEMBER_ME_KEY;

    @Autowired
    private UserDetailsService userDetailService;

    @Bean("grassAuthenticator")
    public AuthenticationProvider authenticator(@Qualifier("grassPasswordEncoder") PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean("rememberMeServices")
    public TokenBasedRememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices =
                new TokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailService);
        return rememberMeServices;
    }

}