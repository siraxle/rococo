package guru.qa.service.db;

import guru.qa.config.Config;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.UUID;

public class AuthDbClient {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public AuthDbClient() {
        Config config = Config.getInstance();
        DataSource dataSource = new DriverManagerDataSource(
                config.authJdbcUrl(),
                config.jdbcUser(),
                config.jdbcPassword()
        );
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public void createUser(String username, String password) {
        UUID userId = UUID.randomUUID();
        String encodedPassword = passwordEncoder.encode(password);

        // Insert user using byte array for UUID
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO `user` (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, true, true, true, true)"
            );
            // Convert UUID to bytes (MySQL expects BINARY(16))
            ps.setBytes(1, uuidToBytes(userId));
            ps.setString(2, username);
            ps.setString(3, encodedPassword);
            return ps;
        });

        // Insert authorities (read and write)
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO authority (id, authority, user_id) VALUES (?, ?, ?)"
            );
            ps.setBytes(1, uuidToBytes(UUID.randomUUID()));
            ps.setString(2, "read");
            ps.setBytes(3, uuidToBytes(userId));
            return ps;
        });

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO authority (id, authority, user_id) VALUES (?, ?, ?)"
            );
            ps.setBytes(1, uuidToBytes(UUID.randomUUID()));
            ps.setString(2, "write");
            ps.setBytes(3, uuidToBytes(userId));
            return ps;
        });
    }

    private byte[] uuidToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (msb >>> (8 * (7 - i)));
            bytes[8 + i] = (byte) (lsb >>> (8 * (7 - i)));
        }
        return bytes;
    }

    public void deleteUser(String username) {
        try {
            byte[] userId = jdbcTemplate.queryForObject(
                    "SELECT id FROM `user` WHERE username = ?",
                    (rs, rowNum) -> rs.getBytes("id"),
                    username
            );
            if (userId != null) {
                jdbcTemplate.update("DELETE FROM authority WHERE user_id = ?", userId);
                jdbcTemplate.update("DELETE FROM `user` WHERE id = ?", userId);
            }
        } catch (Exception e) {
            // User may not exist, ignore
        }
    }
}