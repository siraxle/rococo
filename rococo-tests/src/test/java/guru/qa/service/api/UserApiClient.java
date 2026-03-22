//package guru.qa.service.api;
//
//import guru.qa.model.UserJson;
//import guru.qa.service.RestClient;
//import io.qameta.allure.Step;
//import retrofit2.Response;
//
//import javax.annotation.Nonnull;
//import java.io.IOException;
//import java.util.Optional;
//
//public class UserApiClient extends RestClient {
//
//    private final UserApi userApi;
//
//    public UserApiClient() {
//        super(CFG.gatewayUrl());
//        this.userApi = create(UserApi.class);
//    }
//
//    @Step("Создать пользователя: {username}")
//    @Nonnull
//    public UserJson createUser(String username, String password) {
//        UserJson user = new UserJson(null, username, null, null, null);
//        try {
//            Response<UserJson> response = userApi.createUser(user).execute();
//            if (response.isSuccessful() && response.body() != null) {
//                return response.body();
//            } else {
//                throw new RuntimeException("Failed to create user. Code: " + response.code());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to create user", e);
//        }
//    }
//
//    @Step("Найти пользователя по username: {username}")
//    @Nonnull
//    public Optional<UserJson> findUserByUsername(String username) {
//        try {
//            Response<UserJson> response = userApi.currentUser(username).execute();
//            if (response.isSuccessful()) {
//                return Optional.ofNullable(response.body());
//            } else if (response.code() == 404) {
//                return Optional.empty();
//            } else {
//                throw new RuntimeException("Failed to find user. Code: " + response.code());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to find user", e);
//        }
//    }
//}