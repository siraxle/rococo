package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Geo(
        @JsonProperty("city") String city,
        @JsonProperty("country") Country country
) {}