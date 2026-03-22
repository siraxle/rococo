package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.Painting;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import guru.qa.model.MuseumJson;
import guru.qa.model.PaintingJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RestTest
public class PaintingIntegrationTest {

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
}