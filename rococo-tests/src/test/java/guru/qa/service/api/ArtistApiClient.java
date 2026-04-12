package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.ArtistJson;
import guru.qa.service.RestClient;
import guru.qa.service.ArtistClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

@ParametersAreNonnullByDefault
public class ArtistApiClient extends RestClient implements ArtistClient {

    private final ArtistApi artistApi;

    public ArtistApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.artistApi = create(ArtistApi.class);
    }

    @Override
    @Step("Create artist via API: {artist}")
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

    @Override
    @Step("Get artist by id via API: {id}")
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

    @Override
    @Step("Get all artists via API")
    public List<ArtistJson> getAllArtists() {
        throw new UnsupportedOperationException("Get all artists not supported in API");
    }

    @Override
    @Step("Update artist via API: id={id}, name={name}, biography={biography}, photo={photo}")
    @Nonnull
    public ArtistJson updateArtist(String id, String name, String biography, String photo) {
        ArtistJson artist = new ArtistJson(id, name, biography, photo);
        try {
            Response<ArtistJson> response = artistApi.updateArtist(artist).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to update artist. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update artist", e);
        }
    }

    @Override
    @Step("Delete artist via API: {id}")
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

    @Override
    @Step("Check if artist exists via API: {id}")
    public boolean existsById(String id) {
        try {
            getArtist(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}