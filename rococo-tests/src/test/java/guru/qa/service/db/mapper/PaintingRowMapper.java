package guru.qa.service.db.mapper;

import guru.qa.model.PaintingJson;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaintingRowMapper implements RowMapper<PaintingJson> {

    public static final PaintingRowMapper INSTANCE = new PaintingRowMapper();

    private PaintingRowMapper() {
    }

    @Override
    public PaintingJson mapRow(ResultSet rs, int rowNum) throws SQLException {
        String artistId = formatUuid(rs.getString("artist_id"));
        String museumId = formatUuid(rs.getString("museum_id"));

        PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(artistId, null);
        PaintingJson.MuseumInfo museumInfo = museumId != null ? new PaintingJson.MuseumInfo(museumId) : null;

        return new PaintingJson(
                formatUuid(rs.getString("id")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("photo"),
                null,  // content - нет в БД
                artistInfo,
                museumInfo
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