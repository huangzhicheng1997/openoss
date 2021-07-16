package io.oss.index.service;

import io.oss.util.util.RSAUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${rsa.pubkey]")
    private String RSAPublicKey;

    public String login(String userName, String pwd) {
        String accessToken;
        try {
            accessToken = RSAUtil.encrypt(userName, RSAPublicKey);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return accessToken;
    }
}
