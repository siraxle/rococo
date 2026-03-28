package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import guru.qa.service.ArtistClient;
import guru.qa.service.api.ArtistApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
@DisplayName("Artist Integration Tests")
public class ArtistIntegrationTest {

    private final ArtistClient artistClient = new ArtistApiClient();

    @Artist
    @Test
    @DisplayName("Should create artist via API")
    void shouldCreateArtist(ArtistJson artist) {
        assertThat(artist.id()).isNotNull();
        assertThat(artist.name()).isNotNull();
        assertThat(artist.biography()).isNotNull();
        assertThat(artist.photo()).isNullOrEmpty();
    }

    @Artist
    @Test
    @DisplayName("Should get artist by id via API")
    void shouldGetArtistById(ArtistJson artist) {
        ArtistJson fetchedArtist = artistClient.getArtist(artist.id());

        assertThat(fetchedArtist.id()).isEqualTo(artist.id());
        assertThat(fetchedArtist.name()).isEqualTo(artist.name());
        assertThat(fetchedArtist.biography()).isEqualTo(artist.biography());
        assertThat(fetchedArtist.photo()).isEqualTo(artist.photo());
    }

    @Artist
    @Test
    @DisplayName("Should update artist via API")
    void shouldUpdateArtist(ArtistJson artist) {
        String newName = RandomDataUtils.randomArtistName();
        String newBiography = RandomDataUtils.randomBiography();
        String newPhoto = "new_photo_" + System.currentTimeMillis() + ".jpg";

        ArtistJson updatedArtist = artistClient.updateArtist(
                artist.id(),
                newName,
                newBiography,
                newPhoto
        );

        assertThat(updatedArtist.id()).isEqualTo(artist.id());
        assertThat(updatedArtist.name()).isEqualTo(newName);
        assertThat(updatedArtist.biography()).isEqualTo(newBiography);
        assertThat(updatedArtist.photo()).isEqualTo(newPhoto);
    }

    @Artist
    @Test
    @DisplayName("Should delete artist via API")
    void shouldDeleteArtist(ArtistJson artist) {
        artistClient.deleteArtist(artist.id());

        assertThrows(RuntimeException.class, () -> {
            artistClient.getArtist(artist.id());
        });
    }

    @Test
    @DisplayName("Should return 404 for non-existent artist")
    void shouldReturn404ForNonExistentArtist() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        assertThrows(RuntimeException.class, () -> {
            artistClient.getArtist(nonExistentId);
        });
    }

    @Artist
    @Test
    @DisplayName("Should check if artist exists by id")
    void shouldCheckArtistExists(ArtistJson artist) {
        boolean exists = artistClient.existsById(artist.id());
        assertThat(exists).isTrue();

        boolean nonExistent = artistClient.existsById("00000000-0000-0000-0000-000000000000");
        assertThat(nonExistent).isFalse();
    }

}