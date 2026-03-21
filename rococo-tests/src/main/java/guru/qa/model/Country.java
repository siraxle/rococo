package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Country(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("code") String code
) {}