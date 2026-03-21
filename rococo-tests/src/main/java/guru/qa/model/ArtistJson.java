package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtistJson(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("biography") String biography,
        @JsonProperty("photo") String photo
) {}