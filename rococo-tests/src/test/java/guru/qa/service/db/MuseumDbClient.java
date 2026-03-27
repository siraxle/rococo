package guru.qa.service.db;

import guru.qa.config.Config;
import guru.qa.model.MuseumJson;
import guru.qa.service.MuseumClient;
import io.qameta.allure.Step;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class MuseumDbClient implements MuseumClient {

    private final JdbcTemplate jdbcTemplate;

    public MuseumDbClient() {
        Config config = Config.getInstance();
        DataSource dataSource = new DriverManagerDataSource(
                config.museumJdbcUrl(),
                config.jdbcUser(),
                config.jdbcPassword()
        );
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Step("Create museum in database: {museum}")
    public MuseumJson createMuseum(MuseumJson museum) {
        throw new UnsupportedOperationException("Create museum with countryId required");
    }

    public MuseumJson createMuseum(MuseumJson museum, String countryId) {
        String hexId = museum.id().replace("-", "").toUpperCase();
        String hexCountryId = countryId.replace("-", "").toUpperCase();
        String sql = "INSERT INTO museum (id, title, description, city, address, photo, country_id) " +
                "VALUES (UNHEX(?), ?, ?, ?, ?, ?, UNHEX(?))";
        jdbcTemplate.update(sql, hexId, museum.title(), museum.description(),
                museum.city(), museum.address(), museum.photo(), hexCountryId);
        return museum;
    }

    @Override
    @Step("Get museum from database by id: {id}")
    public MuseumJson getMuseum(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, title, description, city, address, photo, country_id " +
                "FROM museum WHERE HEX(id) = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new MuseumJson(
                formatUuid(rs.getString("id")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("city"),
                rs.getString("address"),
                rs.getString("photo"),
                null
        ), hexId);
    }

    @Override
    @Step("Get all museums from database")
    public List<MuseumJson> getAllMuseums() {
        String sql = "SELECT HEX(id) as id, title, description, city, address, photo, country_id FROM museum";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MuseumJson(
                formatUuid(rs.getString("id")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("city"),
                rs.getString("address"),
                rs.getString("photo"),
                null
        ));
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