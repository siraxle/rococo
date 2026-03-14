package guru.qa.rococo.model;

import java.util.UUID;

public record Country(
        UUID id,
        String name,
        String code
) {}