package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Painting(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("photo") String photo,
        @JsonProperty("artistId") String artistId,
        @JsonProperty("museumId") String museumId
) {}

