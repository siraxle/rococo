package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.MuseumJson;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MuseumApiClient extends RestClient {

    private final MuseumApi museumApi;

    public MuseumApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.museumApi = create(MuseumApi.class);
    }

    @Step("Create museum: {museum}")
    @Nonnull
    public MuseumJson createMuseum(MuseumJson museum) {
        try {
            Response<MuseumJson> response = museumApi.createMuseum(museum).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to create museum. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create museum", e);
        }
    }

    @Step("Get museum by id: {id}")
    @Nonnull
    public MuseumJson getMuseum(String id) {
        try {
            Response<MuseumJson> response = museumApi.getMuseum(id).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get museum. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get museum", e);
        }
    }

    @Step("Delete museum: {id}")
    public void deleteMuseum(String id) {
        try {
            Response<Void> response = museumApi.deleteMuseum(id).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to delete museum. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete museum", e);
        }
    }
}