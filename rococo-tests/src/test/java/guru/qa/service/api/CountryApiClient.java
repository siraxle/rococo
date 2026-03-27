package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.CountryJson;
import guru.qa.model.PageResponse;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public class CountryApiClient extends RestClient {

    private final CountryApi countryApi;

    public CountryApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.countryApi = create(CountryApi.class);
    }

    @Step("Get all countries")
    @Nonnull
    public List<CountryJson> getAllCountries() {
        try {
            Response<PageResponse<CountryJson>> response = countryApi.getAllCountries().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().content();
            } else {
                throw new RuntimeException("Failed to get countries. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get countries", e);
        }
    }

    @Step("Get country by id: {id}")
    @Nonnull
    public CountryJson getCountry(String id) {
        try {
            Response<CountryJson> response = countryApi.getCountry(id).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get country. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get country", e);
        }
    }
}