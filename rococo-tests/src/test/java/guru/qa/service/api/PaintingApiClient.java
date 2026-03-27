package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.PaintingJson;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

public class PaintingApiClient extends RestClient {

    private final PaintingApi paintingApi;

    public PaintingApiClient() {
        super(Config.getInstance().gatewayUrl());
        this.paintingApi = create(PaintingApi.class);
    }

    @Step("Create painting: {painting}")
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

    @Step("Get painting by id: {id}")
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

    @Step("Update painting: {id}")
    @Nonnull
    public PaintingJson updatePainting(String id, PaintingJson painting) {
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

    @Step("Delete painting: {id}")
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
}