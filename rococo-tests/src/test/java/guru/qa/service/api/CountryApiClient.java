package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.CountryJson;
import guru.qa.model.PageResponse;
import guru.qa.service.CountryClient;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

@ParametersAreNonnullByDefault
public class CountryApiClient extends RestClient implements CountryClient {

    private final CountryApi countryApi;

    public CountryApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.countryApi = create(CountryApi.class);
    }

    @Override
    @Step("Get all countries via API")
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

    @Override
    @Step("Get country via API by id: {id}")
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

    @Override
    @Step("Get country via API by code (not supported)")
    public CountryJson getCountryByCode(String code) {
        throw new UnsupportedOperationException("Get country by code via API not supported");
    }

    @Override
    @Step("Check if country exists via API by id")
    public boolean existsById(String id) {
        try {
            getCountry(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Step("Check if country exists via API by code (not supported)")
    public boolean existsByCode(String code) {
        throw new UnsupportedOperationException("Check country exists by code via API not supported");
    }

    @Override
    @Step("Create country via API (not supported)")
    public void createCountry(CountryJson country) {
        throw new UnsupportedOperationException("Create country via API not supported");
    }

    @Override
    @Step("Update country via API (not supported)")
    public void updateCountry(String id, String name, String code) {
        throw new UnsupportedOperationException("Update country via API not supported");
    }

    @Override
    @Step("Delete country via API (not supported)")
    public void deleteCountry(String id) {
        throw new UnsupportedOperationException("Delete country via API not supported");
    }

    @Override
    @Step("Delete country by code via API (not supported)")
    public void deleteCountryByCode(String code) {
        throw new UnsupportedOperationException("Delete country by code via API not supported");
    }

    @Override
    @Step("Get countries count via API (not supported)")
    public int getCountriesCount() {
        throw new UnsupportedOperationException("Get countries count via API not supported");
    }
}