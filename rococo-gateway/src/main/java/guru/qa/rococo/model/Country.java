package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Country(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("code") String code
) {}