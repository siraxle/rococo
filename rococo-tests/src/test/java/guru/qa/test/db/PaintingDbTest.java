package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.PaintingJson;
import guru.qa.service.db.PaintingDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
public class PaintingDbTest {

    private final PaintingDbClient paintingDbClient = new PaintingDbClient();
    private PaintingJson testPainting;
    private String testArtistId;
    private String testMuseumId;

    @BeforeEach
    void setUp() {
        testArtistId = RandomDataUtils.randomId();
        testMuseumId = RandomDataUtils.randomId();

        String id = RandomDataUtils.randomId();
        String title = RandomDataUtils.randomPaintingTitle();
        String description = RandomDataUtils.randomDescription();
        String photo = "photo_" + System.currentTimeMillis() + ".jpg";

        PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(testArtistId, null);
        PaintingJson.MuseumInfo museumInfo = new PaintingJson.MuseumInfo(testMuseumId);

        testPainting = new PaintingJson(id, title, description, photo, null, artistInfo, museumInfo);

        paintingDbClient.createPainting(testPainting);
    }

    @AfterEach
    void tearDown() {
        if (testPainting != null && testPainting.id() != null) {
            paintingDbClient.deletePaintingById(testPainting.id());
        }
    }

    @Test
    void shouldCreateAndSavePaintingToDatabase() {
        boolean exists = paintingDbClient.existsById(testPainting.id());
        assertThat(exists).isTrue();

        PaintingJson dbPainting = paintingDbClient.getPaintingById(testPainting.id());
        assertThat(dbPainting.id()).isEqualTo(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(testPainting.title());
        assertThat(dbPainting.description()).isEqualTo(testPainting.description());
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
        assertThat(dbPainting.photo()).isEqualTo(testPainting.photo());
    }

    @Test
    void shouldReadPaintingFromDatabase() {
        PaintingJson dbPainting = paintingDbClient.getPaintingById(testPainting.id());
        assertThat(dbPainting).isNotNull();
        assertThat(dbPainting.id()).isEqualTo(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(testPainting.title());
        assertThat(dbPainting.description()).isEqualTo(testPainting.description());
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
        assertThat(dbPainting.photo()).isEqualTo(testPainting.photo());
    }

    @Test
    void shouldUpdatePaintingInDatabase() {
        String newTitle = RandomDataUtils.randomPaintingTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        paintingDbClient.updatePainting(testPainting.id(), newTitle, newDescription, newPhoto);

        PaintingJson dbPainting = paintingDbClient.getPaintingById(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(newTitle);
        assertThat(dbPainting.description()).isEqualTo(newDescription);
        assertThat(dbPainting.photo()).isEqualTo(newPhoto);
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
    }

    @Test
    void shouldDeletePaintingFromDatabase() {
        paintingDbClient.deletePaintingById(testPainting.id());

        boolean exists = paintingDbClient.existsById(testPainting.id());
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetAllPaintingsFromDatabase() {
        var paintings = paintingDbClient.getAllPaintings();
        assertThat(paintings).isNotEmpty();
        assertThat(paintings).contains(testPainting);
    }

    @Test
    void shouldNotFindNonExistentPainting() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = paintingDbClient.existsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetNullForNonExistentPainting() {
        String nonExistentId = RandomDataUtils.randomId();

        assertThatThrownBy(() -> paintingDbClient.getPaintingById(nonExistentId))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() {
        String newTitle = RandomDataUtils.randomPaintingTitle();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        paintingDbClient.updatePainting(testPainting.id(), newTitle, null, newPhoto);

        PaintingJson dbPainting = paintingDbClient.getPaintingById(testPainting.id());
        assertThat(dbPainting.title()).isEqualTo(newTitle);
        assertThat(dbPainting.photo()).isEqualTo(newPhoto);
        assertThat(dbPainting.description()).isEqualTo(testPainting.description());
        assertThat(dbPainting.artistId()).isEqualTo(testPainting.artistId());
        assertThat(dbPainting.museumId()).isEqualTo(testPainting.museumId());
    }
}