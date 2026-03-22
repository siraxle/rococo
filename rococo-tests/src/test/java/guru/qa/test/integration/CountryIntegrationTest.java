package guru.qa.test.integration;

import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.CountryJson;
import guru.qa.service.api.CountryApiClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RestTest
public class CountryIntegrationTest {

    private final CountryApiClient countryApiClient = new CountryApiClient();

    @Test
    void shouldGetAllCountries() {
        List<CountryJson> countries = countryApiClient.getAllCountries();
        assertThat(countries).isNotEmpty();
    }
}