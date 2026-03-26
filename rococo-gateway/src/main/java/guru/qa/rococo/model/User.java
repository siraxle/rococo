package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record User(
        @JsonProperty("id") UUID id,
        @JsonProperty("username") String username,
        @JsonProperty("firstname") String firstname,
        @JsonProperty("lastname") String lastname,
        @JsonProperty("avatar") String avatar
) {}