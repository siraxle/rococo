package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserAuthJson(
        @JsonProperty("id") String id,
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("enabled") Boolean enabled,
        @JsonProperty("accountNonExpired") Boolean accountNonExpired,
        @JsonProperty("accountNonLocked") Boolean accountNonLocked,
        @JsonProperty("credentialsNonExpired") Boolean credentialsNonExpired
) {}