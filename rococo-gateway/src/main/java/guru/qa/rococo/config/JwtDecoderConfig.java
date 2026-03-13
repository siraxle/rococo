package guru.qa.rococo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@Profile({"local", "docker"})
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] key = "rococo-local-development-secret-key-for-jwt".getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}