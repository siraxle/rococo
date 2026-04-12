package guru.qa.service.api;

import guru.qa.model.CountryJson;
import guru.qa.model.PageResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CountryApi {

    @GET("/api/country")
    Call<PageResponse<CountryJson>> getAllCountries(@Query("page") int page, @Query("size") int size);

    @GET("/api/country")
    Call<PageResponse<CountryJson>> getAllCountries();

    @GET("/api/country/{id}")
    Call<CountryJson> getCountry(@Path("id") String id);
}