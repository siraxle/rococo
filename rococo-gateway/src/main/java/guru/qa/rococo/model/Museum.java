// Museum.java
package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Museum(
        @JsonProperty("id") UUID id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("city") String city,
        @JsonProperty("address") String address,
        @JsonProperty("photo") String photo,
        @JsonProperty("geo") GeoInfo geo
) {
    public record GeoInfo(
            @JsonProperty("city") String city,
            @JsonProperty("country") CountryInfo country
    ) {}

    public record CountryInfo(
            @JsonProperty("id") UUID id,
            @JsonProperty("name") String name,
            @JsonProperty("code") String code
    ) {}

    public UUID countryId() {
        return geo != null && geo.country() != null ? geo.country().id() : null;
    }
}