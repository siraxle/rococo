package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.CountryJson;
import guru.qa.service.db.GeoDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
public class GeoDbTest {

    private final GeoDbClient geoDbClient = new GeoDbClient();
    private CountryJson testCountry;

    @BeforeEach
    void setUp() {
        String id = RandomDataUtils.randomId();
        String name = RandomDataUtils.randomCountryName();
        String code = RandomDataUtils.randomCountryCode();

        testCountry = new CountryJson(id, name, code);

        geoDbClient.createCountry(testCountry);
    }

    @AfterEach
    void tearDown() {
        if (testCountry != null && testCountry.id() != null) {
            geoDbClient.deleteCountryById(testCountry.id());
        }
    }

    @Test
    void shouldCreateAndSaveCountryToDatabase() {
        boolean exists = geoDbClient.countryExistsById(testCountry.id());
        assertThat(exists).isTrue();

        CountryJson dbCountry = geoDbClient.getCountryById(testCountry.id());
        assertThat(dbCountry.id()).isEqualTo(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(testCountry.name());
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    void shouldReadCountryFromDatabase() {
        CountryJson dbCountry = geoDbClient.getCountryById(testCountry.id());
        assertThat(dbCountry).isNotNull();
        assertThat(dbCountry.id()).isEqualTo(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(testCountry.name());
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    void shouldReadCountryByCode() {
        CountryJson dbCountry = geoDbClient.getCountryByCode(testCountry.code());
        assertThat(dbCountry).isNotNull();
        assertThat(dbCountry.id()).isEqualTo(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(testCountry.name());
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    void shouldUpdateCountryInDatabase() {
        String newName = RandomDataUtils.randomCountryName();
        String newCode = RandomDataUtils.randomCountryCode();

        geoDbClient.updateCountry(testCountry.id(), newName, newCode);

        CountryJson dbCountry = geoDbClient.getCountryById(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(newName);
        assertThat(dbCountry.code()).isEqualTo(newCode);
    }

    @Test
    void shouldDeleteCountryFromDatabase() {
        geoDbClient.deleteCountryById(testCountry.id());

        boolean exists = geoDbClient.countryExistsById(testCountry.id());
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetAllCountriesFromDatabase() {
        var countries = geoDbClient.getAllCountries();
        assertThat(countries).isNotEmpty();
        assertThat(countries).contains(testCountry);
    }

    @Test
    void shouldNotFindNonExistentCountry() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = geoDbClient.countryExistsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetNullForNonExistentCountry() {
        String nonExistentId = RandomDataUtils.randomId();

        assertThatThrownBy(() -> geoDbClient.getCountryById(nonExistentId))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void shouldCheckCountryExistsByCode() {
        boolean exists = geoDbClient.countryExistsByCode(testCountry.code());
        assertThat(exists).isTrue();

        boolean nonExistentExists = geoDbClient.countryExistsByCode("XXX");
        assertThat(nonExistentExists).isFalse();
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() {
        String newName = RandomDataUtils.randomCountryName();

        geoDbClient.updateCountry(testCountry.id(), newName, null);

        CountryJson dbCountry = geoDbClient.getCountryById(testCountry.id());
        assertThat(dbCountry.name()).isEqualTo(newName);
        assertThat(dbCountry.code()).isEqualTo(testCountry.code());
    }

    @Test
    void shouldNotCreateDuplicateCountryByCode() {
        String duplicateCode = testCountry.code();
        String newId = RandomDataUtils.randomId();
        String newName = RandomDataUtils.randomCountryName();

        CountryJson duplicateCountry = new CountryJson(newId, newName, duplicateCode);

        assertThatThrownBy(() -> geoDbClient.createCountry(duplicateCountry))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldGetCountriesCount() {
        int countBefore = geoDbClient.getCountriesCount();

        String extraId = RandomDataUtils.randomId();
        String extraName = RandomDataUtils.randomCountryName();
        String extraCode = RandomDataUtils.randomCountryCode();
        CountryJson extraCountry = new CountryJson(extraId, extraName, extraCode);

        geoDbClient.createCountry(extraCountry);

        int countAfter = geoDbClient.getCountriesCount();
        assertThat(countAfter).isEqualTo(countBefore + 1);

        geoDbClient.deleteCountryById(extraId);
    }
}
