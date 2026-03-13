package guru.qa.rococo.model;

import java.util.UUID;

public record Museum(
        UUID id,
        String title,
        String description,
        String city,
        String address,
        String photo
) {}