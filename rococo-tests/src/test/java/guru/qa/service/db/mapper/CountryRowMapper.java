package guru.qa.service.db.mapper;

import guru.qa.model.CountryJson;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryRowMapper implements RowMapper<CountryJson> {

    public static final CountryRowMapper INSTANCE = new CountryRowMapper();

    private CountryRowMapper() {
    }

    @Override
    public CountryJson mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CountryJson(
                formatUuid(rs.getString("id")),
                rs.getString("name"),
                rs.getString("code")
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