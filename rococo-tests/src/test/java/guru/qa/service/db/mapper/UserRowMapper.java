package guru.qa.service.db.mapper;

import guru.qa.model.UserJson;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<UserJson> {

    public static final UserRowMapper INSTANCE = new UserRowMapper();

    private UserRowMapper() {
    }

    @Override
    public UserJson mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserJson(
                formatUuid(rs.getString("id")),
                rs.getString("username"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("avatar"),
                rs.getString("created_at"),
                null
        );
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