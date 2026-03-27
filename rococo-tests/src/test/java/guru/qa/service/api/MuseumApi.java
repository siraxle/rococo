package guru.qa.service.api;

import guru.qa.model.MuseumJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface MuseumApi {

    @POST("/api/museum")
    Call<MuseumJson> createMuseum(@Body MuseumJson museum);

    @GET("/api/museum/{id}")
    Call<MuseumJson> getMuseum(@Path("id") String id);

    @PATCH("/api/museum")
    Call<MuseumJson> updateMuseum(@Body MuseumJson museum);

    @DELETE("/api/museum/{id}")
    Call<Void> deleteMuseum(@Path("id") String id);
}