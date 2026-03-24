package guru.qa.test.integration;

import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.CountryJson;
import guru.qa.service.api.CountryApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
public class CountryIntegrationTest {

    private final CountryApiClient countryApiClient = new CountryApiClient();

    @Test
    void shouldGetAllCountries() {
        List<CountryJson> countries = countryApiClient.getAllCountries();
        assertThat(countries).isNotEmpty();
    }

    @Test
    void shouldGetCountryById() {
        List<CountryJson> countries = countryApiClient.getAllCountries();
        assertThat(countries).isNotEmpty();

        int randomIndex = RandomDataUtils.randomInt(0, countries.size() - 1);
        CountryJson randomCountry = countries.get(randomIndex);
        assertThat(randomCountry.id()).isNotNull();

        CountryJson fetchedCountry = countryApiClient.getCountry(randomCountry.id());

        assertThat(fetchedCountry.id()).isEqualTo(randomCountry.id());
        assertThat(fetchedCountry.name()).isEqualTo(randomCountry.name());
        assertThat(fetchedCountry.code()).isEqualTo(randomCountry.code());
    }

    @Test
    void shouldReturn404ForNonExistentCountry() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        assertThrows(RuntimeException.class, () -> {
            countryApiClient.getCountry(nonExistentId);
        });
    }

}