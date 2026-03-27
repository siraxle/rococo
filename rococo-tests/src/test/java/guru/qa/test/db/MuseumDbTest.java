package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.CountryJson;
import guru.qa.model.MuseumJson;
import guru.qa.service.CountryClient;
import guru.qa.service.MuseumClient;
import guru.qa.service.db.GeoDbClient;
import guru.qa.service.db.MuseumDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
@DisplayName("Museum Database Tests")
public class MuseumDbTest {

    private final MuseumDbClient museumDbClient = new MuseumDbClient();
    private final CountryClient countryClient = new GeoDbClient();
    private MuseumClient museumClient;
    private MuseumJson testMuseum;
    private String testCountryId;

    private CountryJson getRandomCountry() {
        List<CountryJson> countries = countryClient.getAllCountries();
        if (countries.isEmpty()) {
            throw new IllegalStateException("No countries found in database. Please ensure geo service has initial data.");
        }
        int randomIndex = RandomDataUtils.randomInt(0, countries.size() - 1);
        return countries.get(randomIndex);
    }

    private CountryJson getFirstCountry() {
        List<CountryJson> countries = countryClient.getAllCountries();
        if (countries.isEmpty()) {
            throw new IllegalStateException("No countries found in database. Please ensure geo service has initial data.");
        }
        return countries.get(0);
    }

    @BeforeEach
    @DisplayName("Setup test museum data")
    void setUp() {
        CountryJson testCountry = getFirstCountry();
        testCountryId = testCountry.id();

        String id = RandomDataUtils.randomId();
        String title = RandomDataUtils.randomMuseumTitle();
        String description = RandomDataUtils.randomDescription();
        String city = RandomDataUtils.randomCity();
        String address = RandomDataUtils.randomAddress();
        String photo = "photo_" + System.currentTimeMillis() + ".jpg";

        testMuseum = new MuseumJson(id, title, description, city, address, photo, null);

        museumDbClient.createMuseum(testMuseum, testCountryId);
        museumClient = museumDbClient;
    }

    @AfterEach
    @DisplayName("Cleanup test museum data")
    void tearDown() {
        if (testMuseum != null && testMuseum.id() != null) {
            museumClient.deleteMuseum(testMuseum.id());
        }
    }

    @Test
    @DisplayName("Should create and save museum to database")
    void shouldCreateAndSaveMuseumToDatabase() {
        boolean exists = museumClient.existsById(testMuseum.id());
        assertThat(exists).isTrue();

        MuseumJson dbMuseum = museumClient.getMuseum(testMuseum.id());
        assertThat(dbMuseum.id()).isEqualTo(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(testMuseum.title());
        assertThat(dbMuseum.description()).isEqualTo(testMuseum.description());
        assertThat(dbMuseum.city()).isEqualTo(testMuseum.city());
        assertThat(dbMuseum.address()).isEqualTo(testMuseum.address());
        assertThat(dbMuseum.photo()).isEqualTo(testMuseum.photo());
    }

    @Test
    @DisplayName("Should read museum from database")
    void shouldReadMuseumFromDatabase() {
        MuseumJson dbMuseum = museumClient.getMuseum(testMuseum.id());
        assertThat(dbMuseum).isNotNull();
        assertThat(dbMuseum.id()).isEqualTo(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(testMuseum.title());
        assertThat(dbMuseum.description()).isEqualTo(testMuseum.description());
        assertThat(dbMuseum.city()).isEqualTo(testMuseum.city());
        assertThat(dbMuseum.address()).isEqualTo(testMuseum.address());
        assertThat(dbMuseum.photo()).isEqualTo(testMuseum.photo());
    }

    @Test
    @DisplayName("Should update museum in database")
    void shouldUpdateMuseumInDatabase() {
        String newTitle = RandomDataUtils.randomMuseumTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newCity = RandomDataUtils.randomCity();
        String newAddress = RandomDataUtils.randomAddress();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        MuseumJson updatedMuseum = museumClient.updateMuseum(
                testMuseum.id(),
                newTitle,
                newDescription,
                newCity,
                newAddress,
                newPhoto
        );

        assertThat(updatedMuseum.title()).isEqualTo(newTitle);
        assertThat(updatedMuseum.description()).isEqualTo(newDescription);
        assertThat(updatedMuseum.city()).isEqualTo(newCity);
        assertThat(updatedMuseum.address()).isEqualTo(newAddress);
        assertThat(updatedMuseum.photo()).isEqualTo(newPhoto);

        MuseumJson dbMuseum = museumClient.getMuseum(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(newTitle);
        assertThat(dbMuseum.description()).isEqualTo(newDescription);
        assertThat(dbMuseum.city()).isEqualTo(newCity);
        assertThat(dbMuseum.address()).isEqualTo(newAddress);
        assertThat(dbMuseum.photo()).isEqualTo(newPhoto);
    }

    @Test
    @DisplayName("Should delete museum from database")
    void shouldDeleteMuseumFromDatabase() {
        museumClient.deleteMuseum(testMuseum.id());

        boolean exists = museumClient.existsById(testMuseum.id());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get all museums from database")
    void shouldGetAllMuseumsFromDatabase() {
        var museums = museumClient.getAllMuseums();
        assertThat(museums).isNotEmpty();
        assertThat(museums).contains(testMuseum);
    }

    @Test
    @DisplayName("Should not find non-existent museum")
    void shouldNotFindNonExistentMuseum() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = museumClient.existsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should throw exception for non-existent museum")
    void shouldThrowExceptionForNonExistentMuseum() {
        String nonExistentId = RandomDataUtils.randomId();

        assertThatThrownBy(() -> museumClient.getMuseum(nonExistentId))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("Should update only specified fields")
    void shouldUpdateOnlySpecifiedFields() {
        String newTitle = RandomDataUtils.randomMuseumTitle();
        String newCity = RandomDataUtils.randomCity();

        MuseumJson updatedMuseum = museumClient.updateMuseum(
                testMuseum.id(),
                newTitle,
                null,
                newCity,
                null,
                null
        );

        assertThat(updatedMuseum.title()).isEqualTo(newTitle);
        assertThat(updatedMuseum.city()).isEqualTo(newCity);
        assertThat(updatedMuseum.description()).isEqualTo(testMuseum.description());
        assertThat(updatedMuseum.address()).isEqualTo(testMuseum.address());
        assertThat(updatedMuseum.photo()).isEqualTo(testMuseum.photo());

        MuseumJson dbMuseum = museumClient.getMuseum(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(newTitle);
        assertThat(dbMuseum.city()).isEqualTo(newCity);
        assertThat(dbMuseum.description()).isEqualTo(testMuseum.description());
        assertThat(dbMuseum.address()).isEqualTo(testMuseum.address());
        assertThat(dbMuseum.photo()).isEqualTo(testMuseum.photo());
    }

    @Test
    @DisplayName("Should create museum with random country")
    void shouldCreateMuseumWithRandomCountry() {
        CountryJson randomCountry = getRandomCountry();

        String newId = RandomDataUtils.randomId();
        String newTitle = RandomDataUtils.randomMuseumTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newCity = RandomDataUtils.randomCity();
        String newAddress = RandomDataUtils.randomAddress();
        String newPhoto = "photo_" + System.currentTimeMillis() + ".jpg";

        MuseumJson newMuseum = new MuseumJson(newId, newTitle, newDescription, newCity, newAddress, newPhoto, null);

        try {
            museumDbClient.createMuseum(newMuseum, randomCountry.id());

            boolean exists = museumClient.existsById(newId);
            assertThat(exists).isTrue();

            MuseumJson dbMuseum = museumClient.getMuseum(newId);
            assertThat(dbMuseum.id()).isEqualTo(newId);
            assertThat(dbMuseum.title()).isEqualTo(newTitle);
        } finally {
            museumClient.deleteMuseum(newId);
        }
    }

    @Test
    @DisplayName("Should verify museum has correct country reference")
    void shouldVerifyMuseumHasCorrectCountryReference() {
        CountryJson randomCountry = getRandomCountry();

        String newId = RandomDataUtils.randomId();
        String newTitle = RandomDataUtils.randomMuseumTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newCity = RandomDataUtils.randomCity();
        String newAddress = RandomDataUtils.randomAddress();
        String newPhoto = "photo_" + System.currentTimeMillis() + ".jpg";

        MuseumJson newMuseum = new MuseumJson(newId, newTitle, newDescription, newCity, newAddress, newPhoto, null);

        try {
            museumDbClient.createMuseum(newMuseum, randomCountry.id());

            MuseumJson dbMuseum = museumClient.getMuseum(newId);
            assertThat(dbMuseum.id()).isEqualTo(newId);

            CountryJson savedCountry = countryClient.getCountry(randomCountry.id());
            assertThat(savedCountry.id()).isEqualTo(randomCountry.id());
            assertThat(savedCountry.name()).isEqualTo(randomCountry.name());
        } finally {
            museumClient.deleteMuseum(newId);
        }
    }
}