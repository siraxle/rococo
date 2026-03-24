package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import guru.qa.service.api.ArtistApiClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
public class ArtistIntegrationTest {

    private final ArtistApiClient artistApiClient = new ArtistApiClient();

    @Artist
    @Test
    void shouldCreateArtist(ArtistJson artist) {
        assertThat(artist.id()).isNotNull();
        assertThat(artist.name()).isNotNull();
        assertThat(artist.biography()).isNotNull();
    }

    @Artist
    @Test
    void shouldGetArtistById(ArtistJson artist) {
        ArtistJson fetchedArtist = artistApiClient.getArtist(artist.id());

        assertThat(fetchedArtist.id()).isEqualTo(artist.id());
        assertThat(fetchedArtist.name()).isEqualTo(artist.name());
        assertThat(fetchedArtist.biography()).isEqualTo(artist.biography());
    }

    @Artist
    @Test
    void shouldDeleteArtist(ArtistJson artist) {
        artistApiClient.deleteArtist(artist.id());

        assertThrows(RuntimeException.class, () -> {
            artistApiClient.getArtist(artist.id());
        });
    }
}