package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.Painting;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import guru.qa.model.MuseumJson;
import guru.qa.model.PaintingJson;
import guru.qa.service.PaintingClient;
import guru.qa.service.api.PaintingApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
@DisplayName("Painting Integration Tests")
public class PaintingIntegrationTest {

    private final PaintingClient paintingClient = new PaintingApiClient();

    @Artist
    @Museum
    @Painting
    @Test
    @DisplayName("Should create painting via API")
    void shouldCreatePaintingWithArtistAndMuseum(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
        assertThat(painting.id()).isNotNull();
        assertThat(painting.artistId()).isEqualTo(artist.id());
        assertThat(painting.museumId()).isEqualTo(museum.id());
        assertThat(painting.title()).isNotNull();
        assertThat(painting.description()).isNotNull();
        assertThat(painting.photo()).isNullOrEmpty();
        assertThat(painting.content()).isNull();
    }

    @Artist
    @Museum
    @Painting
    @Test
    @DisplayName("Should get painting by id via API")
    void shouldGetPaintingById(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
        PaintingJson fetched = paintingClient.getPainting(painting.id());

        assertThat(fetched.id()).isEqualTo(painting.id());
        assertThat(fetched.title()).isEqualTo(painting.title());
        assertThat(fetched.description()).isEqualTo(painting.description());
        assertThat(fetched.artistId()).isEqualTo(artist.id());
        assertThat(fetched.museumId()).isEqualTo(museum.id());
        assertThat(fetched.photo()).isEqualTo(painting.photo());
        assertThat(fetched.content()).isEqualTo(painting.content());
    }

    @Artist
    @Museum
    @Painting
    @Test
    @DisplayName("Should update painting via API")
    void shouldUpdatePainting(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
        String newTitle = RandomDataUtils.randomPaintingTitle();
        String newDescription = RandomDataUtils.randomDescription();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        PaintingJson updatedPainting = paintingClient.updatePainting(
                painting.id(),
                newTitle,
                newDescription,
                newPhoto
        );

        assertThat(updatedPainting.id()).isEqualTo(painting.id());
        assertThat(updatedPainting.title()).isEqualTo(newTitle);
        assertThat(updatedPainting.description()).isEqualTo(newDescription);
        assertThat(updatedPainting.content()).isEqualTo(newPhoto);
        assertThat(updatedPainting.artistId()).isEqualTo(artist.id());
        assertThat(updatedPainting.museumId()).isEqualTo(museum.id());
    }

    @Artist
    @Museum
    @Painting
    @Test
    @DisplayName("Should delete painting via API")
    void shouldDeletePainting(PaintingJson painting) {
        paintingClient.deletePainting(painting.id());

        assertThrows(RuntimeException.class, () -> paintingClient.getPainting(painting.id()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent painting")
    void shouldReturn404ForNonExistentPainting() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        assertThrows(RuntimeException.class, () -> {
            paintingClient.getPainting(nonExistentId);
        });
    }

    @Artist
    @Museum
    @Painting
    @Test
    @DisplayName("Should check if painting exists by id")
    void shouldCheckPaintingExists(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
        boolean exists = paintingClient.existsById(painting.id());
        assertThat(exists).isTrue();

        boolean nonExistent = paintingClient.existsById("00000000-0000-0000-0000-000000000000");
        assertThat(nonExistent).isFalse();
    }
}