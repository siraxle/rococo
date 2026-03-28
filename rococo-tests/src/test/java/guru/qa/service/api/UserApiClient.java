package guru.qa.service.api;

import guru.qa.config.Config;
import guru.qa.model.UserJson;
import guru.qa.service.RestClient;
import guru.qa.service.UserClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

@ParametersAreNonnullByDefault
public class UserApiClient extends RestClient implements UserClient {

    private final UserApi userApi;

    public UserApiClient() {
        super(Config.getInstance().userdataUrl());
        this.userApi = create(UserApi.class);
    }

    @Override
    @Step("Create user via API: {user}")
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

    @Override
    @Step("Get user via API by id: {id}")
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

    @Override
    @Step("Get user via API by username: {username}")
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

    @Override
    @Step("Get all users via API (not supported)")
    public List<UserJson> getAllUsers() {
        throw new UnsupportedOperationException("Get all users via API not supported");
    }

    @Override
    @Step("Update user via API: id={id}")
    @Nonnull
    public UserJson updateUser(String id, String firstname, String lastname, String avatar) {
        UserJson user = new UserJson(id, null, firstname, lastname, avatar, null, null);
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

    @Override
    @Step("Delete user via API (not supported)")
    public void deleteUser(String id) {
        throw new UnsupportedOperationException("Delete user via API not supported. Use gRPC instead.");
    }

    @Override
    @Step("Check if user exists via API by id: {id}")
    public boolean existsById(String id) {
        try {
            getUserById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Step("Check if user exists via API by username: {username}")
    public boolean existsByUsername(String username) {
        try {
            getUserByUsername(username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}