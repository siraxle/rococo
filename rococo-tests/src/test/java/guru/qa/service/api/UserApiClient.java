package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.UserJson;
import guru.qa.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

public class UserApiClient extends RestClient {

    private final UserApi userApi;

    public UserApiClient() {
        super(Config.getInstance().userdataUrl());
        this.userApi = create(UserApi.class);
    }

    @Step("Create user: {user.username()}")
    @Nonnull
    public UserJson createUser(UserJson user) {
        try {
            Response<UserJson> response = userApi.createUser(user).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to create user. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Step("Get user by id: {id}")
    @Nonnull
    public UserJson getUserById(String id) {
        try {
            Response<UserJson> response = userApi.getUserById(id).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get user. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get user", e);
        }
    }

    @Step("Get user by username: {username}")
    @Nonnull
    public UserJson getUserByUsername(String username) {
        try {
            Response<UserJson> response = userApi.getUserByUsername(username).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get user by username. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get user by username", e);
        }
    }

    @Step("Update user: {id}")
    @Nonnull
    public UserJson updateUser(String id, UserJson user) {
        try {
            Response<UserJson> response = userApi.updateUser(id, user).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to update user. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Step("Delete user: {id}")
    public void deleteUser(String id) {
        try {
            Response<Void> response = userApi.deleteUser(id).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to delete user. Code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}