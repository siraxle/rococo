package guru.qa.test.integration;

import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.MuseumJson;
import guru.qa.service.api.MuseumApiClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
public class MuseumIntegrationTest {

    private final MuseumApiClient museumApiClient = new MuseumApiClient();

    @User
    @Museum
    @Test
    void shouldCreateMuseum(MuseumJson museum) {
        MuseumJson fetched = museumApiClient.getMuseum(museum.id());
        assertThat(fetched).isEqualTo(museum);
    }

    @User
    @Museum
    @Test
    void shouldDeleteMuseum(MuseumJson museum) {
        museumApiClient.deleteMuseum(museum.id());

        assertThrows(RuntimeException.class, () -> museumApiClient.getMuseum(museum.id()));
    }
}