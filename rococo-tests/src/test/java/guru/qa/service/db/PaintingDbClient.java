package guru.qa.service.db;

import guru.qa.model.PaintingJson;
import guru.qa.service.PaintingClient;
import guru.qa.service.db.mapper.PaintingRowMapper;
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
public class PaintingDbClient implements PaintingClient {

    private final JdbcTemplate jdbcTemplate;
    private final PaintingRowMapper rowMapper = PaintingRowMapper.INSTANCE;

    @Autowired
    public PaintingDbClient(@Qualifier("paintingJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    @Override
    @Step("Create painting in database: {painting}")
    public PaintingJson createPainting(PaintingJson painting) {
        String id = painting.id();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        String hexId = id.replace("-", "").toUpperCase();
        String hexArtistId = painting.artistId().replace("-", "").toUpperCase();
        String hexMuseumId = painting.museumId().replace("-", "").toUpperCase();

        String sql = "INSERT INTO painting (id, title, description, photo, artist_id, museum_id) " +
                "VALUES (UNHEX(?), ?, ?, ?, UNHEX(?), UNHEX(?))";

        jdbcTemplate.update(sql, hexId, painting.title(), painting.description(),
                painting.photo(), hexArtistId, hexMuseumId);

        return new PaintingJson(id, painting.title(), painting.description(),
                painting.photo(), null,
                new PaintingJson.ArtistInfo(painting.artistId(), null),
                new PaintingJson.MuseumInfo(painting.museumId()));
    }

    @Override
    @Step("Get painting from database by id: {id}")
    public PaintingJson getPainting(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, title, description, photo, HEX(artist_id) as artist_id, HEX(museum_id) as museum_id " +
                "FROM painting WHERE HEX(id) = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, hexId);
    }

    @Override
    @Step("Get all paintings from database")
    public List<PaintingJson> getAllPaintings() {
        String sql = "SELECT HEX(id) as id, title, description, photo, HEX(artist_id) as artist_id, HEX(museum_id) as museum_id " +
                "FROM painting";
        return jdbcTemplate.query(sql, rowMapper);
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
}