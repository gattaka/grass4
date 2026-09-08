package cz.gattserver.grass.test;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.impl.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;

@Service
@Primary
public class MockSecurityService implements SecurityService {

    @Value("${explicit.access.salt}")
    private String explicitAccessSalt;

    private UserInfoTO infoTO;

    public MockSecurityService() {
        reset();
    }

    public void reset() {
        infoTO = new UserInfoTO();
        infoTO.setName("mockUser");
        infoTO.setRoles(new HashSet<>());
        infoTO.setId(33333L);
    }

    @Override
    public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
                             HttpServletResponse response) {
        return null;
    }

    @Override
    public UserInfoTO getCurrentUser() {
        return infoTO;
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

    public UserInfoTO getInfoTO() {
        return infoTO;
    }

    public void setInfoTO(UserInfoTO infoTO) {
        this.infoTO = infoTO;
    }

    public void setRoles(HashSet<Role> hashSet) {
        infoTO.setRoles(hashSet);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
    }
}