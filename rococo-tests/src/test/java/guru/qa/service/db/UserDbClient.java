package guru.qa.service.db;

import guru.qa.config.Config;
import guru.qa.model.UserJson;
import guru.qa.service.UserClient;
import io.qameta.allure.Step;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class UserDbClient implements UserClient {

    private final JdbcTemplate jdbcTemplate;

    public UserDbClient() {
        Config config = Config.getInstance();
        DataSource dataSource = new DriverManagerDataSource(
                config.userdataJdbcUrl(),
                config.jdbcUser(),
                config.jdbcPassword()
        );
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Step("Create user in database: {user}")
    public UserJson createUser(UserJson user) {
        String hexId = user.id().replace("-", "").toUpperCase();
        String sql = "INSERT INTO userdata (id, username, firstname, lastname, avatar) " +
                "VALUES (UNHEX(?), ?, ?, ?, ?)";
        jdbcTemplate.update(sql, hexId, user.username(), user.firstname(),
                user.lastname(), user.avatar());
        return user;
    }

    @Override
    @Step("Get user from database by id: {id}")
    public UserJson getUserById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, username, firstname, lastname, avatar, created_at " +
                "FROM userdata WHERE HEX(id) = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new UserJson(
                formatUuid(rs.getString("id")),
                rs.getString("username"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("avatar"),
                rs.getString("created_at"),
                null
        ), hexId);
    }

    @Override
    @Step("Get user from database by username: {username}")
    public UserJson getUserByUsername(String username) {
        String sql = "SELECT HEX(id) as id, username, firstname, lastname, avatar, created_at " +
                "FROM userdata WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new UserJson(
                formatUuid(rs.getString("id")),
                rs.getString("username"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("avatar"),
                rs.getString("created_at"),
                null
        ), username);
    }

    @Override
    @Step("Get all users from database")
    public List<UserJson> getAllUsers() {
        String sql = "SELECT HEX(id) as id, username, firstname, lastname, avatar, created_at FROM userdata";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserJson(
                formatUuid(rs.getString("id")),
                rs.getString("username"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("avatar"),
                rs.getString("created_at"),
                null
        ));
    }

    @Override
    @Step("Update user in database: id={id}, firstname={firstname}, lastname={lastname}, avatar={avatar}")
    public UserJson updateUser(String id, String firstname, String lastname, String avatar) {
        String hexId = id.replace("-", "").toUpperCase();
        StringBuilder sql = new StringBuilder("UPDATE userdata SET ");
        List<Object> params = new ArrayList<>();

        if (firstname != null) {
            sql.append("firstname = ?, ");
            params.add(firstname);
        }
        if (lastname != null) {
            sql.append("lastname = ?, ");
            params.add(lastname);
        }
        if (avatar != null) {
            sql.append("avatar = ?, ");
            params.add(avatar);
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No fields to update");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE HEX(id) = ?");
        params.add(hexId);

        jdbcTemplate.update(sql.toString(), params.toArray());
        return getUserById(id);
    }

    @Override
    @Step("Delete user from database by id: {id}")
    public void deleteUser(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "DELETE FROM userdata WHERE HEX(id) = ?";
        jdbcTemplate.update(sql, hexId);
    }

    @Override
    @Step("Check if user exists in database by id: {id}")
    public boolean existsById(String id) {
        try {
            String hexId = id.replace("-", "").toUpperCase();
            String sql = "SELECT COUNT(*) FROM userdata WHERE HEX(id) = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hexId);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    @Step("Check if user exists in database by username: {username}")
    public boolean existsByUsername(String username) {
        try {
            String sql = "SELECT COUNT(*) FROM userdata WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private String formatUuid(String hex) {
        if (hex == null || hex.length() != 32) {
            return hex;
        }
        return String.format("%s-%s-%s-%s-%s",
                hex.substring(0, 8),
                hex.substring(8, 12),
                hex.substring(12, 16),
                hex.substring(16, 20),
                hex.substring(20)
        ).toLowerCase();
    }
}