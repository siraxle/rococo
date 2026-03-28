package guru.qa.service.db;

import guru.qa.config.Config;
import guru.qa.model.CountryJson;
import guru.qa.service.CountryClient;
import guru.qa.service.db.mapper.CountryRowMapper;
import io.qameta.allure.Step;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class GeoDbClient implements CountryClient {

    private final JdbcTemplate jdbcTemplate;
    private final CountryRowMapper rowMapper = CountryRowMapper.INSTANCE;

    public GeoDbClient() {
        Config config = Config.getInstance();
        DataSource dataSource = new DriverManagerDataSource(
                config.geoJdbcUrl(),
                config.jdbcUser(),
                config.jdbcPassword()
        );
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Step("Get all countries from database")
    public List<CountryJson> getAllCountries() {
        String sql = "SELECT HEX(id) as id, name, code FROM country";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Step("Get country from database by id: {id}")
    public CountryJson getCountry(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT HEX(id) as id, name, code FROM country WHERE HEX(id) = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, hexId);
    }

    @Override
    @Step("Get country from database by code: {code}")
    public CountryJson getCountryByCode(String code) {
        String sql = "SELECT HEX(id) as id, name, code FROM country WHERE code = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, code);
    }

    @Override
    @Step("Check if country exists in database by id: {id}")
    public boolean existsById(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "SELECT COUNT(*) FROM country WHERE HEX(id) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hexId);
        return count != null && count > 0;
    }

    @Override
    @Step("Check if country exists in database by code: {code}")
    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM country WHERE code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != null && count > 0;
    }

    @Override
    @Step("Create country in database: {country}")
    public void createCountry(CountryJson country) {
        String hexId = country.id().replace("-", "").toUpperCase();
        String sql = "INSERT INTO country (id, name, code) VALUES (UNHEX(?), ?, ?)";
        jdbcTemplate.update(sql, hexId, country.name(), country.code());
    }

    @Override
    @Step("Update country in database: id={id}, name={name}, code={code}")
    public void updateCountry(String id, String name, String code) {
        String hexId = id.replace("-", "").toUpperCase();
        StringBuilder sql = new StringBuilder("UPDATE country SET ");
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append("name = ?, ");
            params.add(name);
        }
        if (code != null) {
            sql.append("code = ?, ");
            params.add(code);
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No fields to update");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE HEX(id) = ?");
        params.add(hexId);

        jdbcTemplate.update(sql.toString(), params.toArray());
    }

    @Override
    @Step("Delete country from database by id: {id}")
    public void deleteCountry(String id) {
        String hexId = id.replace("-", "").toUpperCase();
        String sql = "DELETE FROM country WHERE HEX(id) = ?";
        jdbcTemplate.update(sql, hexId);
    }

    @Override
    @Step("Delete country from database by code: {code}")
    public void deleteCountryByCode(String code) {
        String sql = "DELETE FROM country WHERE code = ?";
        jdbcTemplate.update(sql, code);
    }

    @Override
    @Step("Get countries count from database")
    public int getCountriesCount() {
        String sql = "SELECT COUNT(*) FROM country";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}