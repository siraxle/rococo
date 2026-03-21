package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Museum(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("city") String city,
        @JsonProperty("address") String address,
        @JsonProperty("photo") String photo,
        @JsonProperty("geo") Geo geo
) {}