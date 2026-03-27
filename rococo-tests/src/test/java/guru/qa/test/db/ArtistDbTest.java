package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.ArtistJson;
import guru.qa.service.db.ArtistDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DbTest
public class ArtistDbTest {

    private final ArtistDbClient artistDbClient = new ArtistDbClient();
    private ArtistJson testArtist;

    @BeforeEach
    void setUp() {
        String id = RandomDataUtils.randomId();
        String name = RandomDataUtils.randomArtistName();
        String biography = RandomDataUtils.randomBiography();
        String photo = "photo_" + System.currentTimeMillis() + ".jpg";

        testArtist = new ArtistJson(id, name, biography, photo);

        artistDbClient.createArtist(testArtist);
    }

    @AfterEach
    void tearDown() {
        if (testArtist != null && testArtist.id() != null) {
            artistDbClient.deleteArtistById(testArtist.id());
        }
    }

    @Test
    void shouldCreateAndSaveArtistToDatabase() {
        boolean exists = artistDbClient.existsById(testArtist.id());
        assertThat(exists).isTrue();

        ArtistJson dbArtist = artistDbClient.getArtistById(testArtist.id());
        assertThat(dbArtist.id()).isEqualTo(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(testArtist.name());
        assertThat(dbArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(dbArtist.photo()).isEqualTo(testArtist.photo());
    }

    @Test
    void shouldReadArtistFromDatabase() {
        ArtistJson dbArtist = artistDbClient.getArtistById(testArtist.id());
        assertThat(dbArtist).isNotNull();
        assertThat(dbArtist.name()).isEqualTo(testArtist.name());
        assertThat(dbArtist.biography()).isEqualTo(testArtist.biography());
        assertThat(dbArtist.photo()).isEqualTo(testArtist.photo());
    }

    @Test
    void shouldUpdateArtistInDatabase() {
        String newName = RandomDataUtils.randomArtistName();
        String newBiography = RandomDataUtils.randomBiography();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        artistDbClient.updateArtist(testArtist.id(), newName, newBiography, newPhoto);

        ArtistJson dbArtist = artistDbClient.getArtistById(testArtist.id());
        assertThat(dbArtist.name()).isEqualTo(newName);
        assertThat(dbArtist.biography()).isEqualTo(newBiography);
        assertThat(dbArtist.photo()).isEqualTo(newPhoto);
    }

    @Test
    void shouldDeleteArtistFromDatabase() {
        artistDbClient.deleteArtistById(testArtist.id());

        boolean exists = artistDbClient.existsById(testArtist.id());
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetAllArtistsFromDatabase() {
        var artists = artistDbClient.getAllArtists();
        assertThat(artists).isNotEmpty();
        assertThat(artists).contains(testArtist);
    }
}