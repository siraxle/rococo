package guru.qa.test.db;

import guru.qa.config.DatabaseConfig;
import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.PaintingJson;
import guru.qa.service.PaintingClient;
import guru.qa.service.db.PaintingDbClient;
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
@DisplayName("Painting Database Tests")
public class PaintingDbTest {

    @Autowired
    private PaintingDbClient paintingDbClient;

    private PaintingClient paintingClient;
    private PaintingJson testPainting;
    private String testArtistId;
    private String testMuseumId;

    @BeforeEach
    @DisplayName("Setup test painting data")
    void setUp() {
        paintingClient = paintingDbClient;

        testArtistId = RandomDataUtils.randomId();
        testMuseumId = RandomDataUtils.randomId();

        String id = RandomDataUtils.randomId();
        String title = RandomDataUtils.randomPaintingTitle();
        String description = RandomDataUtils.randomDescription();
        String photo = "photo_" + System.currentTimeMillis() + ".jpg";

        PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(testArtistId, null);
        PaintingJson.MuseumInfo museumInfo = new PaintingJson.MuseumInfo(testMuseumId);

        testPainting = new PaintingJson(id, title, description, photo, null, artistInfo, museumInfo);

        paintingClient.createPainting(testPainting);
    }

    @AfterEach
    @DisplayName("Cleanup test painting data")
    void tearDown() {
        if (testPainting != null && testPainting.id() != null) {
            paintingClient.deletePainting(testPainting.id());
        }
    }

    @Test
    @DisplayName("Should create and save painting to database")
    void shouldCreateAndSavePaintingToDatabase() {
        boolean exists = paintingClient.existsById(testPainting.id());
        assertThat(exists).isTrue();

        PaintingJson dbPainting = paintingClient.getPainting(testPainting.id());
        assertThat(dbPainting.id()).isEqualTo(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(testPainting.title());
        assertThat(dbPainting.description()).isEqualTo(testPainting.description());
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
        assertThat(dbPainting.photo()).isEqualTo(testPainting.photo());
    }

    @Test
    @DisplayName("Should read painting from database")
    void shouldReadPaintingFromDatabase() {
        PaintingJson dbPainting = paintingClient.getPainting(testPainting.id());
        assertThat(dbPainting).isNotNull();
        assertThat(dbPainting.id()).isEqualTo(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(testPainting.title());
        assertThat(dbPainting.description()).isEqualTo(testPainting.description());
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
        assertThat(dbPainting.photo()).isEqualTo(testPainting.photo());
    }

    @Test
    @DisplayName("Should update painting in database")
    void shouldUpdatePaintingInDatabase() {
        String newTitle = RandomDataUtils.randomPaintingTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        PaintingJson updatedPainting = paintingClient.updatePainting(testPainting.id(), newTitle, newDescription, newPhoto);

        assertThat(updatedPainting.title()).isEqualTo(newTitle);
        assertThat(updatedPainting.description()).isEqualTo(newDescription);
        assertThat(updatedPainting.photo()).isEqualTo(newPhoto);
        assertThat(updatedPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(updatedPainting.museumId()).isEqualTo(testPainting.museumId());

        PaintingJson dbPainting = paintingClient.getPainting(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(newTitle);
        assertThat(dbPainting.description()).isEqualTo(newDescription);
        assertThat(dbPainting.photo()).isEqualTo(newPhoto);
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
    }

    @Test
    @DisplayName("Should delete painting from database")
    void shouldDeletePaintingFromDatabase() {
        paintingClient.deletePainting(testPainting.id());

        boolean exists = paintingClient.existsById(testPainting.id());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get all paintings from database")
    void shouldGetAllPaintingsFromDatabase() {
        var paintings = paintingClient.getAllPaintings();
        assertThat(paintings).isNotEmpty();

        boolean found = paintings.stream()
                .anyMatch(p -> p.id().equals(testPainting.id()));
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("Should not find non-existent painting")
    void shouldNotFindNonExistentPainting() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = paintingClient.existsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should throw exception for non-existent painting")
    void shouldThrowExceptionForNonExistentPainting() {
        String nonExistentId = RandomDataUtils.randomId();

        assertThatThrownBy(() -> paintingClient.getPainting(nonExistentId))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("Should update only specified fields")
    void shouldUpdateOnlySpecifiedFields() {
        String newTitle = RandomDataUtils.randomPaintingTitle();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        PaintingJson updatedPainting = paintingClient.updatePainting(testPainting.id(), newTitle, null, newPhoto);

        assertThat(updatedPainting.title()).isEqualTo(newTitle);
        assertThat(updatedPainting.photo()).isEqualTo(newPhoto);
        assertThat(updatedPainting.description()).isEqualTo(testPainting.description());
        assertThat(updatedPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(updatedPainting.museumId()).isEqualTo(testPainting.museumId());

        PaintingJson dbPainting = paintingClient.getPainting(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(newTitle);
        assertThat(dbPainting.photo()).isEqualTo(newPhoto);
        assertThat(dbPainting.description()).isEqualTo(testPainting.description());
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
    }
}