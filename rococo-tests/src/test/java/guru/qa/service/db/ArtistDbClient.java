package guru.qa.service.db;

import guru.qa.config.Config;
import guru.qa.model.ArtistJson;
import guru.qa.service.ArtistClient;
import io.qameta.allure.Step;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class ArtistDbClient implements ArtistClient {

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

    @Override
    @Step("Create artist in database: {artist}")
    public ArtistJson createArtist(ArtistJson artist) {
        String hexId = artist.id().replace("-", "").toUpperCase();
        String sql = "INSERT INTO artist (id, name, biography, photo) VALUES (UNHEX(?), ?, ?, ?)";
        jdbcTemplate.update(sql, hexId, artist.name(), artist.biography(), artist.photo());
        return artist;
    }

    @Override
    @Step("Get artist from database by id: {id}")
    public ArtistJson getArtist(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, name, biography, photo FROM artist WHERE HEX(id) = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ArtistJson(
                formatUuid(rs.getString("id")),
                rs.getString("name"),
                rs.getString("biography"),
                rs.getString("photo")
        ), hexId);
    }

    @Override
    @Step("Get all artists from database")
    public List<ArtistJson> getAllArtists() {
        String sql = "SELECT HEX(id) as id, name, biography, photo FROM artist";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ArtistJson(
                formatUuid(rs.getString("id")),
                rs.getString("name"),
                rs.getString("biography"),
                rs.getString("photo")
        ));
    }

    @Override
    @Step("Update artist in database: id={id}, name={name}, biography={biography}, photo={photo}")
    public ArtistJson updateArtist(String id, String name, String biography, String photo) {
        String hexId = id.replace("-", "").toUpperCase();
        StringBuilder sql = new StringBuilder("UPDATE artist SET ");
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append("name = ?, ");
            params.add(name);
        }
        if (biography != null) {
            sql.append("biography = ?, ");
            params.add(biography);
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
        return getArtist(id);
    }

    @Override
    @Step("Delete artist from database by id: {id}")
    public void deleteArtist(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "DELETE FROM artist WHERE HEX(id) = ?";
        jdbcTemplate.update(sql, hexId);
    }

    @Override
    @Step("Check if artist exists in database by id: {id}")
    public boolean existsById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT COUNT(*) FROM artist WHERE HEX(id) = ?";
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