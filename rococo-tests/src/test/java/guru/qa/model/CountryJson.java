package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CountryJson(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("code") String code
) {}