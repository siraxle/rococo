package guru.qa.service.api;

import guru.qa.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {

    @GET("/api/user/{id}")
    Call<UserJson> getUserById(@Path("id") String id);

    @GET("/api/user/username/{username}")
    Call<UserJson> getUserByUsername(@Path("username") String username);

    @POST("/api/user")
    Call<UserJson> createUser(@Body UserJson user);

    @PATCH("/api/user/{id}")
    Call<UserJson> updateUser(@Path("id") String id, @Body UserJson user);

    @DELETE("/api/user/{id}")
    Call<Void> deleteUser(@Path("id") String id);
}