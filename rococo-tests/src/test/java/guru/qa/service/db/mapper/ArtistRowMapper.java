package guru.qa.service.db.mapper;

import guru.qa.model.ArtistJson;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistRowMapper implements RowMapper<ArtistJson> {

    public static final ArtistRowMapper INSTANCE = new ArtistRowMapper();

    private ArtistRowMapper() {
    }

    @Override
    public ArtistJson mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ArtistJson(
                formatUuid(rs.getString("id")),
                rs.getString("name"),
                rs.getString("biography"),
                rs.getString("photo")
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