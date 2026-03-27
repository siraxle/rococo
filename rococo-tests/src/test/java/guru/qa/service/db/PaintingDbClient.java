package guru.qa.service.db;

import guru.qa.config.Config;
import guru.qa.model.PaintingJson;
import guru.qa.service.PaintingClient;
import io.qameta.allure.Step;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class PaintingDbClient implements PaintingClient {

    private final JdbcTemplate jdbcTemplate;

    public PaintingDbClient() {
        Config config = Config.getInstance();
        DataSource dataSource = new DriverManagerDataSource(
                config.paintingJdbcUrl(),
                config.jdbcUser(),
                config.jdbcPassword()
        );
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Step("Create painting in database: {painting}")
    public PaintingJson createPainting(PaintingJson painting) {
        String hexId = painting.id().replace("-", "").toUpperCase();
        String hexArtistId = painting.artistId().replace("-", "").toUpperCase();
        String hexMuseumId = painting.museumId().replace("-", "").toUpperCase();

        String sql = "INSERT INTO painting (id, title, description, photo, artist_id, museum_id) " +
                "VALUES (UNHEX(?), ?, ?, ?, UNHEX(?), UNHEX(?))";

        jdbcTemplate.update(sql, hexId, painting.title(), painting.description(),
                painting.photo(), hexArtistId, hexMuseumId);
        return painting;
    }

    @Override
    @Step("Get painting from database by id: {id}")
    public PaintingJson getPainting(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, title, description, photo, HEX(artist_id) as artist_id, HEX(museum_id) as museum_id " +
                "FROM painting WHERE HEX(id) = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            String artistId = formatUuid(rs.getString("artist_id"));
            String museumId = formatUuid(rs.getString("museum_id"));

            PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(artistId, null);
            PaintingJson.MuseumInfo museumInfo = museumId != null ? new PaintingJson.MuseumInfo(museumId) : null;

            return new PaintingJson(
                    formatUuid(rs.getString("id")),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("photo"),
                    null,
                    artistInfo,
                    museumInfo
            );
        }, hexId);
    }

    @Override
    @Step("Get all paintings from database")
    public List<PaintingJson> getAllPaintings() {
        String sql = "SELECT HEX(id) as id, title, description, photo, HEX(artist_id) as artist_id, HEX(museum_id) as museum_id " +
                "FROM painting";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String artistId = formatUuid(rs.getString("artist_id"));
            String museumId = formatUuid(rs.getString("museum_id"));

            PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(artistId, null);
            PaintingJson.MuseumInfo museumInfo = museumId != null ? new PaintingJson.MuseumInfo(museumId) : null;

            return new PaintingJson(
                    formatUuid(rs.getString("id")),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("photo"),
                    null,
                    artistInfo,
                    museumInfo
            );
        });
    }

    @Override
    @Step("Update painting in database: id={id}, title={title}, description={description}, photo={photo}")
    public PaintingJson updatePainting(String id, String title, String description, String photo) {
        String hexId = id.replace("-", "").toUpperCase();
        StringBuilder sql = new StringBuilder("UPDATE painting SET ");
        List<Object> params = new ArrayList<>();

        if (title != null) {
            sql.append("title = ?, ");
            params.add(title);
        }
        if (description != null) {
            sql.append("description = ?, ");
            params.add(description);
        }
        if (photo != null) {
            sql.append("photo = ?, ");
            params.add(photo);
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No fields to update");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE HEX(id) = ?");
        params.add(hexId);

        jdbcTemplate.update(sql.toString(), params.toArray());
        return getPainting(id);
    }

    @Override
    @Step("Delete painting from database by id: {id}")
    public void deletePainting(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "DELETE FROM painting WHERE HEX(id) = ?";
        jdbcTemplate.update(sql, hexId);
    }

    @Override
    @Step("Check if painting exists in database by id: {id}")
    public boolean existsById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT COUNT(*) FROM painting WHERE HEX(id) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hexId);
        return count != null && count > 0;
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