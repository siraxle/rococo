package guru.qa.service.api;

import guru.qa.model.CountryJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface CountryApi {

    @GET("/api/country")
    Call<List<CountryJson>> getAllCountries();

    @GET("/api/country/{id}")
    Call<CountryJson> getCountry(@Path("id") String id);
}