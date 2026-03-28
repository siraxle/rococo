package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.MuseumJson;
import guru.qa.service.MuseumClient;
import guru.qa.service.api.MuseumApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
@DisplayName("Museum Integration Tests")
public class MuseumIntegrationTest {

    private final MuseumClient museumClient = new MuseumApiClient();

    @User
    @Museum
    @Test
    @DisplayName("Should create museum via API")
    void shouldCreateMuseum(MuseumJson museum) {
        assertThat(museum.id()).isNotNull();
        assertThat(museum.title()).isNotNull();
        assertThat(museum.description()).isNotNull();
        assertThat(museum.city()).isNotNull();
        assertThat(museum.address()).isNotNull();
        assertThat(museum.photo()).isNullOrEmpty();
        assertThat(museum.geo()).isNotNull();
        assertThat(museum.geo().city()).isEqualTo(museum.city());
        assertThat(museum.geo().country()).isNotNull();
    }

    @User
    @Museum
    @Test
    @DisplayName("Should get museum by id via API")
    void shouldGetMuseumById(MuseumJson museum) {
        MuseumJson fetched = museumClient.getMuseum(museum.id());

        assertThat(fetched.id()).isEqualTo(museum.id());
        assertThat(fetched.title()).isEqualTo(museum.title());
        assertThat(fetched.description()).isEqualTo(museum.description());
        assertThat(fetched.city()).isEqualTo(museum.city());
        assertThat(fetched.address()).isEqualTo(museum.address());
        assertThat(fetched.photo()).isEqualTo(museum.photo());
        assertThat(fetched.geo()).isEqualTo(museum.geo());
    }

    @User
    @Museum
    @Test
    @DisplayName("Should update museum via API")
    void shouldUpdateMuseum(MuseumJson museum) {
        String newTitle = RandomDataUtils.randomMuseumTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newCity = RandomDataUtils.randomCity();
        String newAddress = RandomDataUtils.randomAddress();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        MuseumJson updatedMuseum = museumClient.updateMuseum(
                museum.id(),
                newTitle,
                newDescription,
                newCity,
                newAddress,
                newPhoto
        );

        assertThat(updatedMuseum.id()).isEqualTo(museum.id());
        assertThat(updatedMuseum.title()).isEqualTo(newTitle);
        assertThat(updatedMuseum.description()).isEqualTo(newDescription);
        assertThat(updatedMuseum.address()).isEqualTo(newAddress);
        assertThat(updatedMuseum.photo()).isEqualTo(newPhoto);
        assertThat(updatedMuseum.geo().country()).isEqualTo(museum.geo().country());
    }

    @User
    @Museum
    @Test
    @DisplayName("Should delete museum via API")
    void shouldDeleteMuseum(MuseumJson museum) {
        museumClient.deleteMuseum(museum.id());

        assertThrows(RuntimeException.class, () -> museumClient.getMuseum(museum.id()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent museum")
    void shouldReturn404ForNonExistentMuseum() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        assertThrows(RuntimeException.class, () -> {
            museumClient.getMuseum(nonExistentId);
        });
    }

    @User
    @Museum
    @Test
    @DisplayName("Should check if museum exists by id")
    void shouldCheckMuseumExists(MuseumJson museum) {
        boolean exists = museumClient.existsById(museum.id());
        assertThat(exists).isTrue();

        boolean nonExistent = museumClient.existsById("00000000-0000-0000-0000-000000000000");
        assertThat(nonExistent).isFalse();
    }
}