package guru.qa.service.db;

import guru.qa.model.MuseumJson;
import guru.qa.service.MuseumClient;
import guru.qa.service.db.mapper.MuseumRowMapper;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@Repository
public class MuseumDbClient implements MuseumClient {

    private final JdbcTemplate jdbcTemplate;
    private final MuseumRowMapper rowMapper = MuseumRowMapper.INSTANCE;

    @Autowired
    public MuseumDbClient(@Qualifier("museumJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Step("Create museum in database: {museum}")
    public MuseumJson createMuseum(MuseumJson museum) {
        throw new UnsupportedOperationException("Create museum with countryId required");
    }

    public MuseumJson createMuseum(MuseumJson museum, String countryId) {
        String hexId = museum.id().replace("-", "").toUpperCase();
        String hexCountryId = countryId.replace("-", "").toUpperCase();

        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

        String sql = "INSERT INTO museum (id, title, description, city, address, photo, country_id, created_at, updated_at) " +
                "VALUES (UNHEX(?), ?, ?, ?, ?, ?, UNHEX(?), ?, ?)";

        jdbcTemplate.update(sql, hexId, museum.title(), museum.description(),
                museum.city(), museum.address(), museum.photo(), hexCountryId, now, now);

        return museum;
    }

    @Override
    @Step("Get museum from database by id: {id}")
    public MuseumJson getMuseum(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, title, description, city, address, photo, country_id " +
                "FROM museum WHERE HEX(id) = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, hexId);
    }

    @Override
    @Step("Get all museums from database")
    public List<MuseumJson> getAllMuseums() {
        String sql = "SELECT HEX(id) as id, title, description, city, address, photo, country_id FROM museum";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Step("Update museum in database: id={id}, title={title}, description={description}, city={city}, address={address}, photo={photo}")
    public MuseumJson updateMuseum(String id, String title, String description,
                                   String city, String address, String photo) {
        String hexId = id.replace("-", "").toUpperCase();
        StringBuilder sql = new StringBuilder("UPDATE museum SET ");
        List<Object> params = new ArrayList<>();

        if (title != null) {
            sql.append("title = ?, ");
            params.add(title);
        }
        if (description != null) {
            sql.append("description = ?, ");
            params.add(description);
        }
        if (city != null) {
            sql.append("city = ?, ");
            params.add(city);
        }
        if (address != null) {
            sql.append("address = ?, ");
            params.add(address);
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
        return getMuseum(id);
    }

    @Override
    @Step("Delete museum from database by id: {id}")
    public void deleteMuseum(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "DELETE FROM museum WHERE HEX(id) = ?";
        jdbcTemplate.update(sql, hexId);
    }

    @Override
    @Step("Check if museum exists in database by id: {id}")
    public boolean existsById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT COUNT(*) FROM museum WHERE HEX(id) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hexId);
        return count != null && count > 0;
    }
}