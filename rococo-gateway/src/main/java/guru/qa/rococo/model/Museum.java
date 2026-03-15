package guru.qa.rococo.model;

import java.util.Map;
import java.util.UUID;

public record Museum(
        UUID id,
        String title,
        String description,
        String city,
        String address,
        String photo,
        Map<String, Object> geo
) {}