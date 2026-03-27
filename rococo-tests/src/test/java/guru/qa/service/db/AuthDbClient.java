package guru.qa.service.db;

import guru.qa.config.Config;
import guru.qa.service.AuthClient;
import io.qameta.allure.Step;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.UUID;

public class AuthDbClient implements AuthClient {

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

    @Override
    @Step("Create user in database: {username}")
    public void createUser(String username, String password) {
        try {
            UUID userId = UUID.randomUUID();
            String encodedPassword = passwordEncoder.encode(password);

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO `user` (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                "VALUES (?, ?, ?, true, true, true, true)"
                );
                ps.setBytes(1, uuidToBytes(userId));
                ps.setString(2, username);
                ps.setString(3, encodedPassword);
                return ps;
            });

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
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("User already exists: " + username, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + username, e);
        }
    }

    @Override
    @Step("Delete user from database: {username}")
    public void deleteUser(String username) {
        try {
            String deleteAuthoritiesSql = "DELETE FROM authority WHERE user_id = (SELECT id FROM `user` WHERE username = ?)";
            jdbcTemplate.update(deleteAuthoritiesSql, username);

            String deleteUserSql = "DELETE FROM `user` WHERE username = ?";
            jdbcTemplate.update(deleteUserSql, username);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to delete user: " + username, e);
        }
    }

    @Override
    @Step("Check if user exists in database: {username}")
    public boolean userExists(String username) {
        try {
            String sql = "SELECT COUNT(*) FROM `user` WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    @Step("Get user password from database: {username}")
    public String getUserPassword(String username) {
        try {
            String sql = "SELECT password FROM `user` WHERE username = ?";
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Step("Get user authorities count from database: {username}")
    public int getUserAuthoritiesCount(String username) {
        try {
            String sql = "SELECT COUNT(*) FROM authority a " +
                    "JOIN `user` u ON a.user_id = u.id " +
                    "WHERE u.username = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, username);
        } catch (DataAccessException e) {
            return 0;
        }
    }

    @Override
    @Step("Register user via database (not supported)")
    public void registerUser(String username, String password) {
        throw new UnsupportedOperationException("Register user via database not supported");
    }

    @Override
    @Step("Login via database (not supported)")
    public String login(String username, String password) {
        throw new UnsupportedOperationException("Login via database not supported");
    }

    @Override
    @Step("Register and login via database (not supported)")
    public String registerAndLogin(String username, String password) {
        throw new UnsupportedOperationException("Register and login via database not supported");
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
}