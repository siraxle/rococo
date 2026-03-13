package guru.qa.rococo.model;

import java.util.UUID;

public record Artist(
        UUID id,
        String name,
        String biography,
        String photo
) {}