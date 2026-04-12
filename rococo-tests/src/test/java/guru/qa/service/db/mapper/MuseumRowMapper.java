package guru.qa.service.db.mapper;

import guru.qa.model.MuseumJson;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MuseumRowMapper implements RowMapper<MuseumJson> {

    public static final MuseumRowMapper INSTANCE = new MuseumRowMapper();

    private MuseumRowMapper() {
    }

    @Override
    public MuseumJson mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MuseumJson(
                formatUuid(rs.getString("id")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("city"),
                rs.getString("address"),
                rs.getString("photo"),
                null  // geo - информация о стране хранится в geo-сервисе
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