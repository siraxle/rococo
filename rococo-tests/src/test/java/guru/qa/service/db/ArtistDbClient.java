package guru.qa.service.db;

import guru.qa.config.Config;
import guru.qa.model.ArtistJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

public class ArtistDbClient {

    private final JdbcTemplate jdbcTemplate;

    public ArtistDbClient() {
        Config config = Config.getInstance();
        DataSource dataSource = new DriverManagerDataSource(
                config.artistJdbcUrl(),
                config.jdbcUser(),
                config.jdbcPassword()
        );
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean existsById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT COUNT(*) FROM artist WHERE HEX(id) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hexId);
        return count != null && count > 0;
    }

    public ArtistJson getArtistById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, name, biography, photo FROM artist WHERE HEX(id) = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ArtistJson(
                formatUuid(rs.getString("id")),
                rs.getString("name"),
                rs.getString("biography"),
                rs.getString("photo")
        ), hexId);
    }

    public List<ArtistJson> getAllArtists() {
        String sql = "SELECT HEX(id) as id, name, biography, photo FROM artist";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ArtistJson(
                formatUuid(rs.getString("id")),
                rs.getString("name"),
                rs.getString("biography"),
                rs.getString("photo")
        ));
    }

    public void updateArtist(String id, String name, String biography, String photo) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "UPDATE artist SET name = ?, biography = ?, photo = ? WHERE HEX(id) = ?";
        jdbcTemplate.update(sql, name, biography, photo, hexId);
    }

    public void deleteArtistById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "DELETE FROM artist WHERE HEX(id) = ?";
        jdbcTemplate.update(sql, hexId);
    }

    public void createArtist(ArtistJson artist) {
        String hexId = artist.id().replace("-", "").toUpperCase();
        String sql = "INSERT INTO artist (id, name, biography, photo) VALUES (UNHEX(?), ?, ?, ?)";
        jdbcTemplate.update(sql, hexId, artist.name(), artist.biography(), artist.photo());
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