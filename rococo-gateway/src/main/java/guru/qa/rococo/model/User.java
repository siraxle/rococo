package guru.qa.rococo.model;

import java.util.UUID;

public record User(
        UUID id,
        String username,
        String firstname,
        String lastname,
        String avatar
) {}