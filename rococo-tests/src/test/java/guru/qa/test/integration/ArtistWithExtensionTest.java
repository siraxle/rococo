package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RestTest
public class ArtistWithExtensionTest {

    @Artist
    @Test
    void shouldCreateArtistViaExtension(ArtistJson artist) {
        assertThat(artist.id()).isNotNull();
        assertThat(artist.name()).isNotNull();
        assertThat(artist.biography()).isNotNull();
    }
}