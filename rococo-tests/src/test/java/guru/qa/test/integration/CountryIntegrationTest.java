package guru.qa.test.integration;

import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.CountryJson;
import guru.qa.service.CountryClient;
import guru.qa.service.api.CountryApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
@DisplayName("Country Integration Tests")
public class CountryIntegrationTest {

    private final CountryClient countryClient = new CountryApiClient();

    @Test
    @DisplayName("Should get all countries via API")
    void shouldGetAllCountries() {
        List<CountryJson> countries = countryClient.getAllCountries();
        assertThat(countries).isNotEmpty();
    }

    @Test
    @DisplayName("Should get country by id via API")
    void shouldGetCountryById() {
        List<CountryJson> countries = countryClient.getAllCountries();
        assertThat(countries).isNotEmpty();

        CountryJson firstCountry = countries.get(0);
        CountryJson fetched = countryClient.getCountry(firstCountry.id());

        assertThat(fetched.id()).isEqualTo(firstCountry.id());
        assertThat(fetched.name()).isEqualTo(firstCountry.name());
        assertThat(fetched.code()).isEqualTo(firstCountry.code());
    }

    @Test
    @DisplayName("Should return 404 for non-existent country")
    void shouldReturn404ForNonExistentCountry() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        assertThrows(RuntimeException.class, () -> {
            countryClient.getCountry(nonExistentId);
        });
    }

    @Test
    @DisplayName("Should check if country exists by id")
    void shouldCheckCountryExists() {
        List<CountryJson> countries = countryClient.getAllCountries();
        assertThat(countries).isNotEmpty();

        CountryJson firstCountry = countries.get(0);
        boolean exists = countryClient.existsById(firstCountry.id());
        assertThat(exists).isTrue();

        boolean nonExistent = countryClient.existsById("00000000-0000-0000-0000-000000000000");
        assertThat(nonExistent).isFalse();
    }
}