package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.ArtistJson;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

public class ArtistApiClient extends RestClient {

    private final ArtistApi artistApi;

    public ArtistApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.artistApi = create(ArtistApi.class);
    }

    @Step("Create artist: {artist}")
    @Nonnull
    public ArtistJson createArtist(ArtistJson artist) {
        try {
            Response<ArtistJson> response = artistApi.createArtist(artist).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to create artist. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create artist", e);
        }
    }

    @Step("Get artist by id: {id}")
    @Nonnull
    public ArtistJson getArtist(String id) {
        try {
            Response<ArtistJson> response = artistApi.getArtist(id).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get artist. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get artist", e);
        }
    }

    @Step("Delete artist: {id}")
    public void deleteArtist(String id) {
        try {
            Response<Void> response = artistApi.deleteArtist(id).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to delete artist. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete artist", e);
        }
    }
}