package guru.qa.service.db;

import guru.qa.model.ArtistJson;
import guru.qa.service.ArtistClient;
import guru.qa.service.db.mapper.ArtistRowMapper;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
@Repository
public class ArtistDbClient implements ArtistClient {

    private final JdbcTemplate jdbcTemplate;
    private final ArtistRowMapper rowMapper = ArtistRowMapper.INSTANCE;

    @Autowired
    public ArtistDbClient(@Qualifier("artistJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Step("Create artist in database: {artist}")
    public ArtistJson createArtist(ArtistJson artist) {
        String id = artist.id();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        String hexId = id.replace("-", "").toUpperCase();

        String sql = "INSERT INTO artist (id, name, biography, photo) VALUES (UNHEX(?), ?, ?, ?)";
        jdbcTemplate.update(sql, hexId, artist.name(), artist.biography(), artist.photo());

        return new ArtistJson(id, artist.name(), artist.biography(), artist.photo());
    }

    @Override
    @Step("Get artist from database by id: {id}")
    public ArtistJson getArtist(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, name, biography, photo FROM artist WHERE HEX(id) = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, hexId);
    }

    @Override
    @Step("Get all artists from database")
    public List<ArtistJson> getAllArtists() {
        String sql = "SELECT HEX(id) as id, name, biography, photo FROM artist";
        return jdbcTemplate.query(sql, rowMapper);
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
}