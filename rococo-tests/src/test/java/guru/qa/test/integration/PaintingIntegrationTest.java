package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.Painting;
import guru.qa.jupiter.annotation.User;
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

    @User
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

    @User
    @Artist
    @Museum
    @Painting
    @Test
    void shouldDeletePainting(ArtistJson artist, MuseumJson museum, PaintingJson painting) {
        paintingApiClient.deletePainting(painting.id());

        assertThrows(RuntimeException.class, () -> paintingApiClient.getPainting(painting.id()));
    }
}