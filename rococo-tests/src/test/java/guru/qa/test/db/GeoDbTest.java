package guru.qa.test.db;

import guru.qa.config.DatabaseConfig;
import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.CountryJson;
import guru.qa.service.CountryClient;
import guru.qa.service.db.GeoDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
@SpringJUnitConfig(classes = DatabaseConfig.class)
@DisplayName("Geo Database Tests")
public class GeoDbTest {

    @Autowired
    private GeoDbClient geoDbClient;

    private CountryClient countryClient;
    private CountryJson testCountry;

    @BeforeEach
    @DisplayName("Setup test country data")
    void setUp() {
        countryClient = geoDbClient;

        String id = RandomDataUtils.randomId();
        String name = RandomDataUtils.randomCountryName();
        String code = RandomDataUtils.randomCountryCode();

        testCountry = new CountryJson(id, name, code);
        countryClient.createCountry(testCountry);
    }

    @AfterEach
    @DisplayName("Cleanup test country data")
    void tearDown() {
        if (testCountry != null && testCountry.id() != null && countryClient.existsById(testCountry.id())) {
            countryClient.deleteCountry(testCountry.id());
        }
    }

    @Test
    @DisplayName("Should create and save country to database")
    void shouldCreateAndSaveCountryToDatabase() {
        boolean exists = countryClient.existsById(testCountry.id());
        assertThat(exists).isTrue();

        CountryJson dbCountry = countryClient.getCountry(testCountry.id());
        assertThat(dbCountry.id()).isEqualTo(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(testCountry.name());
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    @DisplayName("Should read country from database")
    void shouldReadCountryFromDatabase() {
        CountryJson dbCountry = countryClient.getCountry(testCountry.id());
        assertThat(dbCountry).isNotNull();
        assertThat(dbCountry.id()).isEqualTo(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(testCountry.name());
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    @DisplayName("Should read country by code")
    void shouldReadCountryByCode() {
        CountryJson dbCountry = countryClient.getCountryByCode(testCountry.code());
        assertThat(dbCountry).isNotNull();
        assertThat(dbCountry.id()).isEqualTo(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(testCountry.name());
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    @DisplayName("Should update country in database")
    void shouldUpdateCountryInDatabase() {
        String newName = RandomDataUtils.randomCountryName();
        String newCode = RandomDataUtils.randomCountryCode();

        countryClient.updateCountry(testCountry.id(), newName, newCode);

        CountryJson dbCountry = countryClient.getCountry(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(newName);
        assertThat(dbCountry.code()).isEqualTo(newCode);
    }

    @Test
    @DisplayName("Should delete country from database")
    void shouldDeleteCountryFromDatabase() {
        countryClient.deleteCountry(testCountry.id());

        boolean exists = countryClient.existsById(testCountry.id());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get all countries from database")
    void shouldGetAllCountriesFromDatabase() {
        var countries = countryClient.getAllCountries();
        assertThat(countries).isNotEmpty();

        boolean found = countries.stream()
                .anyMatch(c -> c.id().equals(testCountry.id()));
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("Should not find non-existent country")
    void shouldNotFindNonExistentCountry() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = countryClient.existsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should throw exception for non-existent country")
    void shouldThrowExceptionForNonExistentCountry() {
        String nonExistentId = RandomDataUtils.randomId();

        assertThatThrownBy(() -> countryClient.getCountry(nonExistentId))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("Should check country exists by code")
    void shouldCheckCountryExistsByCode() {
        boolean exists = countryClient.existsByCode(testCountry.code());
        assertThat(exists).isTrue();

        boolean nonExistentExists = countryClient.existsByCode("XXX");
        assertThat(nonExistentExists).isFalse();
    }

    @Test
    @DisplayName("Should update only specified fields")
    void shouldUpdateOnlySpecifiedFields() {
        String newName = RandomDataUtils.randomCountryName();

        countryClient.updateCountry(testCountry.id(), newName, null);

        CountryJson dbCountry = countryClient.getCountry(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(newName);
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    @DisplayName("Should not create duplicate country by code")
    void shouldNotCreateDuplicateCountryByCode() {
        String duplicateCode = testCountry.code();
        String newId = RandomDataUtils.randomId();
        String newName = RandomDataUtils.randomCountryName();

        CountryJson duplicateCountry = new CountryJson(newId, newName, duplicateCode);

        assertThatThrownBy(() -> countryClient.createCountry(duplicateCountry))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should get countries count")
    void shouldGetCountriesCount() {
        int countBefore = countryClient.getCountriesCount();

        String extraId = RandomDataUtils.randomId();
        String extraName = RandomDataUtils.randomCountryName();
        String extraCode = RandomDataUtils.randomCountryCode();
        CountryJson extraCountry = new CountryJson(extraId, extraName, extraCode);

        countryClient.createCountry(extraCountry);

        int countAfter = countryClient.getCountriesCount();
        assertThat(countAfter).isEqualTo(countBefore + 1);

        countryClient.deleteCountry(extraId);
    }
}