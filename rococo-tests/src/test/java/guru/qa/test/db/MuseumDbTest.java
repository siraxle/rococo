package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.MuseumJson;
import guru.qa.service.db.MuseumDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
public class MuseumDbTest {

    private final MuseumDbClient museumDbClient = new MuseumDbClient();
    private MuseumJson testMuseum;
    private String testCountryId = "68fc6442-1f06-11f1-b22a-0a2d8eac97b2";

    @BeforeEach
    void setUp() {
        String id = RandomDataUtils.randomId();
        String title = RandomDataUtils.randomMuseumTitle();
        String description = RandomDataUtils.randomDescription();
        String city = RandomDataUtils.randomCity();
        String address = RandomDataUtils.randomAddress();
        String photo = "photo_" + System.currentTimeMillis() + ".jpg";

        testMuseum = new MuseumJson(id, title, description, city, address, photo, null);

        museumDbClient.createMuseum(testMuseum, testCountryId);
    }

    @AfterEach
    void tearDown() {
        if (testMuseum != null && testMuseum.id() != null) {
            museumDbClient.deleteMuseumById(testMuseum.id());
        }
    }

    @Test
    void shouldCreateAndSaveMuseumToDatabase() {
        boolean exists = museumDbClient.existsById(testMuseum.id());
        assertThat(exists).isTrue();

        MuseumJson dbMuseum = museumDbClient.getMuseumById(testMuseum.id());
        assertThat(dbMuseum.id()).isEqualTo(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(testMuseum.title());
        assertThat(dbMuseum.description()).isEqualTo(testMuseum.description());
        assertThat(dbMuseum.city()).isEqualTo(testMuseum.city());
        assertThat(dbMuseum.address()).isEqualTo(testMuseum.address());
        assertThat(dbMuseum.photo()).isEqualTo(testMuseum.photo());
    }

    @Test
    void shouldReadMuseumFromDatabase() {
        MuseumJson dbMuseum = museumDbClient.getMuseumById(testMuseum.id());
        assertThat(dbMuseum).isNotNull();
        assertThat(dbMuseum.id()).isEqualTo(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(testMuseum.title());
        assertThat(dbMuseum.description()).isEqualTo(testMuseum.description());
        assertThat(dbMuseum.city()).isEqualTo(testMuseum.city());
        assertThat(dbMuseum.address()).isEqualTo(testMuseum.address());
        assertThat(dbMuseum.photo()).isEqualTo(testMuseum.photo());
    }

    @Test
    void shouldUpdateMuseumInDatabase() {
        String newTitle = RandomDataUtils.randomMuseumTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newCity = RandomDataUtils.randomCity();
        String newAddress = RandomDataUtils.randomAddress();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        museumDbClient.updateMuseum(
                testMuseum.id(),
                newTitle,
                newDescription,
                newCity,
                newAddress,
                newPhoto
        );

        MuseumJson dbMuseum = museumDbClient.getMuseumById(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(newTitle);
        assertThat(dbMuseum.description()).isEqualTo(newDescription);
        assertThat(dbMuseum.city()).isEqualTo(newCity);
        assertThat(dbMuseum.address()).isEqualTo(newAddress);
        assertThat(dbMuseum.photo()).isEqualTo(newPhoto);
    }

    @Test
    void shouldDeleteMuseumFromDatabase() {
        museumDbClient.deleteMuseumById(testMuseum.id());

        boolean exists = museumDbClient.existsById(testMuseum.id());
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetAllMuseumsFromDatabase() {
        var museums = museumDbClient.getAllMuseums();
        assertThat(museums).isNotEmpty();
        assertThat(museums).contains(testMuseum);
    }

    @Test
    void shouldNotFindNonExistentMuseum() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = museumDbClient.existsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetNullForNonExistentMuseum() {
        String nonExistentId = RandomDataUtils.randomId();

        assertThatThrownBy(() -> museumDbClient.getMuseumById(nonExistentId))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() {
        String newTitle = RandomDataUtils.randomMuseumTitle();
        String newCity = RandomDataUtils.randomCity();

        museumDbClient.updateMuseum(
                testMuseum.id(),
                newTitle,
                null,
                newCity,
                null,
                null
        );

        MuseumJson dbMuseum = museumDbClient.getMuseumById(testMuseum.id());
        assertThat(dbMuseum.title()).isEqualTo(newTitle);
        assertThat(dbMuseum.city()).isEqualTo(newCity);
        assertThat(dbMuseum.description()).isEqualTo(testMuseum.description());
        assertThat(dbMuseum.address()).isEqualTo(testMuseum.address());
        assertThat(dbMuseum.photo()).isEqualTo(testMuseum.photo());
    }
}