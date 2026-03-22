package guru.qa.service.api;

import guru.qa.model.ArtistJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface ArtistApi {

    @POST("/api/artist")
    Call<ArtistJson> createArtist(@Body ArtistJson artist);

    @GET("/api/artist/{id}")
    Call<ArtistJson> getArtist(@Path("id") String id);

    @DELETE("/api/artist/{id}")
    Call<Void> deleteArtist(@Path("id") String id);
}