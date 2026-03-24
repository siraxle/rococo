package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.Painting;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import guru.qa.model.MuseumJson;
import guru.qa.model.PaintingJson;
import guru.qa.service.api.PaintingApiClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
public class PaintingIntegrationTest {

    private final PaintingApiClient paintingApiClient = new PaintingApiClient();

    @Artist
    @Museum
    @Painting
    @Test
    void shouldCreatePaintingWithArtistAndMuseum(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
        assertThat(painting.id()).isNotNull();
        assertThat(painting.artistId()).isEqualTo(artist.id());
        assertThat(painting.museumId()).isEqualTo(museum.id());
        assertThat(painting.title()).isNotNull();
    }

    @Artist
    @Museum
    @Painting
    @Test
    void shouldGetPaintingById(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
        PaintingJson fetched = paintingApiClient.getPainting(painting.id());

        assertThat(fetched.id()).isEqualTo(painting.id());
        assertThat(fetched.title()).isEqualTo(painting.title());
        assertThat(fetched.description()).isEqualTo(painting.description());
        assertThat(fetched.artistId()).isEqualTo(artist.id());
        assertThat(fetched.museumId()).isEqualTo(museum.id());
    }

    @Artist
    @Museum
    @Painting
    @Test
    void shouldDeletePainting(PaintingJson painting) {
        paintingApiClient.deletePainting(painting.id());

        assertThrows(RuntimeException.class, () -> paintingApiClient.getPainting(painting.id()));
    }
}