//package guru.qa.service.api;
//
//import guru.qa.model.UserJson;
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.GET;
//import retrofit2.http.POST;
//import retrofit2.http.Query;
//
//public interface UserApi {
//
//    @POST("/api/user")
//    Call<UserJson> createUser(@Body UserJson user);
//
//    @GET("/api/user")
//    Call<UserJson> currentUser(@Query("username") String username);
//}