package guru.qa.rococo.model;

import guru.qa.rococo.entity.CountryEntity;

import java.util.Map;
import java.util.UUID;

public record Museum(
        UUID id,
        String title,
        String description,
        String city,
        String address,
        String photo,
        CountryEntity country,
        Map<String, Object> geo) {}