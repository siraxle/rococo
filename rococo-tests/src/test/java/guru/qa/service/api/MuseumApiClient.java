package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.MuseumJson;
import guru.qa.service.MuseumClient;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

@ParametersAreNonnullByDefault
public class MuseumApiClient extends RestClient implements MuseumClient {

    private final MuseumApi museumApi;

    public MuseumApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.museumApi = create(MuseumApi.class);
    }

    @Override
    @Step("Create museum via API: {museum}")
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

    @Override
    @Step("Get museum via API by id: {id}")
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

    @Override
    @Step("Get all museums via API (not supported)")
    public List<MuseumJson> getAllMuseums() {
        throw new UnsupportedOperationException("Get all museums via API not supported");
    }

    @Override
    @Step("Update museum via API: id={id}, title={title}, description={description}, city={city}, address={address}, photo={photo}")
    @Nonnull
    public MuseumJson updateMuseum(String id, String title, String description,
                                   String city, String address, String photo) {
        MuseumJson museum = new MuseumJson(id, title, description, city, address, photo, null);
        try {
            Response<MuseumJson> response = museumApi.updateMuseum(museum).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to update museum. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update museum", e);
        }
    }

    @Override
    @Step("Delete museum via API: {id}")
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

    @Override
    @Step("Check if museum exists via API: {id}")
    public boolean existsById(String id) {
        try {
            getMuseum(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}