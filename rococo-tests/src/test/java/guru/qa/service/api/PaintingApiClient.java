package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.PaintingJson;
import guru.qa.service.PaintingClient;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public class PaintingApiClient extends RestClient implements PaintingClient {

    private final PaintingApi paintingApi;

    public PaintingApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.paintingApi = create(PaintingApi.class);
    }

    @Override
    @Step("Create painting via API: {painting}")
    @Nonnull
    public PaintingJson createPainting(PaintingJson painting) {
        try {
            Response<PaintingJson> response = paintingApi.createPainting(painting).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to create painting. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create painting", e);
        }
    }

    @Override
    @Step("Get painting via API by id: {id}")
    @Nonnull
    public PaintingJson getPainting(String id) {
        try {
            Response<PaintingJson> response = paintingApi.getPainting(id).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get painting. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get painting", e);
        }
    }

    @Override
    @Step("Get all paintings via API (not supported)")
    public List<PaintingJson> getAllPaintings() {
        throw new UnsupportedOperationException("Get all paintings via API not supported");
    }

    @Override
    @Step("Update painting via API: id={id}")
    @Nonnull
    public PaintingJson updatePainting(String id, String title, String description, String photo) {
        PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(null, null);
        PaintingJson.MuseumInfo museumInfo = new PaintingJson.MuseumInfo(null);
        PaintingJson painting = new PaintingJson(id, title, description, photo, null, artistInfo, museumInfo);
        try {
            Response<PaintingJson> response = paintingApi.updatePainting(painting).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to update painting. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update painting", e);
        }
    }

    @Override
    @Step("Delete painting via API: {id}")
    public void deletePainting(String id) {
        try {
            Response<Void> response = paintingApi.deletePainting(id).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to delete painting. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete painting", e);
        }
    }

    @Override
    @Step("Check if painting exists via API: {id}")
    public boolean existsById(String id) {
        try {
            getPainting(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}