package cz.gattserver.grass.test;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.impl.LoginResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashSet;

@Service
@Primary
public class MockSecurityService implements SecurityService {

    @Value("${explicit.access.salt}")
    private String explicitAccessSalt;

    @Setter
    @Getter
    private UserInfoTO infoTO;

    public MockSecurityService() {
        resetAsMock();
    }

    public void resetAsMock() {
        infoTO = new UserInfoTO();
        infoTO.setName("mockUser");
        infoTO.setRoles(new HashSet<>());
        infoTO.setId(33333L);
    }

    public void resetAsEmpty() {
        infoTO = new UserInfoTO();
    }

    public void resetAs(Long userId1, boolean admin) {
        infoTO.setId(userId1);
        if (admin) {
            infoTO.getRoles().add(CoreRole.ADMIN);
        } else {
            infoTO.getRoles().remove(CoreRole.ADMIN);
        }
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
    public String computeAccessHash(String value) {
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

    public void setRoles(HashSet<Role> hashSet) {
        infoTO.setRoles(hashSet);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
    }
}