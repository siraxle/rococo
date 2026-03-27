package guru.qa.service.api;

import guru.qa.model.PaintingJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface PaintingApi {

    @POST("/api/painting")
    Call<PaintingJson> createPainting(@Body PaintingJson painting);

    @GET("/api/painting/{id}")
    Call<PaintingJson> getPainting(@Path("id") String id);

    @PATCH("/api/painting")
    Call<PaintingJson> updatePainting(@Body PaintingJson painting);

    @DELETE("/api/painting/{id}")
    Call<Void> deletePainting(@Path("id") String id);
}