package guru.qa.test.db;

import guru.qa.config.DatabaseConfig;
import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.ArtistJson;
import guru.qa.service.ArtistClient;
import guru.qa.service.db.ArtistDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DbTest
@SpringJUnitConfig(classes = DatabaseConfig.class)
@DisplayName("Artist Database Tests")
public class ArtistDbTest {

    @Autowired
    private ArtistDbClient artistDbClient;
    private ArtistClient artistClient;
    private ArtistJson testArtist;

    @BeforeEach
    @DisplayName("Setup test artist data")
    void setUp() {
        artistClient = artistDbClient;

        String id = RandomDataUtils.randomId();
        String name = RandomDataUtils.randomArtistName();
        String biography = RandomDataUtils.randomBiography();
        String photo = "photo_" + System.currentTimeMillis() + ".jpg";

        testArtist = new ArtistJson(id, name, biography, photo);
        artistClient.createArtist(testArtist);
    }

    @AfterEach
    @DisplayName("Cleanup test artist data")
    void tearDown() {
        if (testArtist != null && testArtist.id() != null) {
            artistClient.deleteArtist(testArtist.id());
        }
    }

    @Test
    @DisplayName("Should create and save artist to database")
    void shouldCreateAndSaveArtistToDatabase() {
        boolean exists = artistClient.existsById(testArtist.id());
        assertThat(exists).isTrue();

        ArtistJson dbArtist = artistClient.getArtist(testArtist.id());
        assertThat(dbArtist.id()).isEqualTo(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(testArtist.name());
        assertThat(dbArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(dbArtist.photo()).isEqualTo(testArtist.photo());
    }

    @Test
    @DisplayName("Should read artist from database")
    void shouldReadArtistFromDatabase() {
        ArtistJson dbArtist = artistClient.getArtist(testArtist.id());
        assertThat(dbArtist).isNotNull();
        assertThat(dbArtist.id()).isEqualTo(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(testArtist.name());
        assertThat(dbArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(dbArtist.photo()).isEqualTo(testArtist.photo());
    }

    @Test
    @DisplayName("Should update artist in database")
    void shouldUpdateArtistInDatabase() {
        String newName = RandomDataUtils.randomArtistName();
        String newBiography = RandomDataUtils.randomBiography();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        ArtistJson updatedArtist = artistClient.updateArtist(testArtist.id(), newName, newBiography, newPhoto);

        assertThat(updatedArtist.name()).isEqualTo(newName);
        assertThat(updatedArtist.biography()).isEqualTo(newBiography);
        assertThat(updatedArtist.photo()).isEqualTo(newPhoto);

        ArtistJson dbArtist = artistClient.getArtist(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(newName);
        assertThat(dbArtist.biography()).isEqualTo(newBiography);
        assertThat(dbArtist.photo()).isEqualTo(newPhoto);
    }

    @Test
    @DisplayName("Should delete artist from database")
    void shouldDeleteArtistFromDatabase() {
        artistClient.deleteArtist(testArtist.id());

        boolean exists = artistClient.existsById(testArtist.id());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get all artists from database")
    void shouldGetAllArtistsFromDatabase() {
        var artists = artistClient.getAllArtists();
        assertThat(artists).isNotEmpty();

        boolean found = artists.stream()
                .anyMatch(a -> a.id().equals(testArtist.id()));
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("Should not find non-existent artist")
    void shouldNotFindNonExistentArtist() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = artistClient.existsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should update only name field")
    void shouldUpdateOnlyNameField() {
        String newName = RandomDataUtils.randomArtistName();

        ArtistJson updatedArtist = artistClient.updateArtist(testArtist.id(), newName, null, null);

        assertThat(updatedArtist.name()).isEqualTo(newName);
        assertThat(updatedArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(updatedArtist.photo()).isEqualTo(testArtist.photo());

        ArtistJson dbArtist = artistClient.getArtist(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(newName);
        assertThat(dbArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(dbArtist.photo()).isEqualTo(testArtist.photo());
    }

    @Test
    @DisplayName("Should update only biography field")
    void shouldUpdateOnlyBiographyField() {
        String newBiography = RandomDataUtils.randomBiography();

        ArtistJson updatedArtist = artistClient.updateArtist(testArtist.id(), null, newBiography, null);

        assertThat(updatedArtist.name()).isEqualTo(testArtist.name());
        assertThat(updatedArtist.biography()).isEqualTo(newBiography);
        assertThat(updatedArtist.photo()).isEqualTo(testArtist.photo());

        ArtistJson dbArtist = artistClient.getArtist(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(testArtist.name());
        assertThat(dbArtist.biography()).isEqualTo(newBiography);
        assertThat(dbArtist.photo()).isEqualTo(testArtist.photo());
    }

    @Test
    @DisplayName("Should update only photo field")
    void shouldUpdateOnlyPhotoField() {
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        ArtistJson updatedArtist = artistClient.updateArtist(testArtist.id(), null, null, newPhoto);

        assertThat(updatedArtist.name()).isEqualTo(testArtist.name());
        assertThat(updatedArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(updatedArtist.photo()).isEqualTo(newPhoto);

        ArtistJson dbArtist = artistClient.getArtist(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(testArtist.name());
        assertThat(dbArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(dbArtist.photo()).isEqualTo(newPhoto);
    }
}