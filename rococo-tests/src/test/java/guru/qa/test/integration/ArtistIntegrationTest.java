package guru.qa.test.integration;

import guru.qa.jupiter.annotation.ApiLogin;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import guru.qa.model.UserJson;
import guru.qa.service.api.ArtistApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
public class ArtistIntegrationTest {

    private final ArtistApiClient artistApiClient = new ArtistApiClient();

    @ApiLogin
    @User
    @Test
    void shouldCreateArtist(UserJson user) {
        String expectedName = RandomDataUtils.randomArtistName();
        String expectedBiography = RandomDataUtils.randomBiography();

        ArtistJson artistToCreate = new ArtistJson(null, expectedName, expectedBiography, null);
        ArtistJson createdArtist = artistApiClient.createArtist(artistToCreate);

        assertThat(createdArtist.id()).isNotNull();
        assertThat(createdArtist.name()).isEqualTo(expectedName);
        assertThat(createdArtist.biography()).isEqualTo(expectedBiography);
    }

    @ApiLogin
    @User
    @Test
    void shouldGetArtistById(UserJson user) {
        String expectedName = RandomDataUtils.randomArtistName();
        String expectedBiography = RandomDataUtils.randomBiography();

        ArtistJson artistToCreate = new ArtistJson(null, expectedName, expectedBiography, null);
        ArtistJson createdArtist = artistApiClient.createArtist(artistToCreate);

        ArtistJson fetchedArtist = artistApiClient.getArtist(createdArtist.id());

        assertThat(fetchedArtist.id()).isEqualTo(createdArtist.id());
        assertThat(fetchedArtist.name()).isEqualTo(expectedName);
        assertThat(fetchedArtist.biography()).isEqualTo(expectedBiography);
    }

    @ApiLogin
    @User
    @Test
    void shouldDeleteArtist(UserJson user) {
        ArtistJson artistToCreate = new ArtistJson(null, RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(), null);
        ArtistJson createdArtist = artistApiClient.createArtist(artistToCreate);

        artistApiClient.deleteArtist(createdArtist.id());

        // Check that artist is deleted (get returns 404)
        assertThrows(RuntimeException.class, () -> {
            artistApiClient.getArtist(createdArtist.id());
        });
    }
}