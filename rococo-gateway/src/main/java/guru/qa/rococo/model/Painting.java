package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Painting(
        @JsonProperty("id") UUID id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("content") String content,
        @JsonProperty("artist") ArtistInfo artist,
        @JsonProperty("museum") MuseumInfo museum
) {
    public record ArtistInfo(
            @JsonProperty("id") UUID id,
            @JsonProperty("name") String name
    ) {}

    public record MuseumInfo(
            @JsonProperty("id") UUID id
    ) {}

    // Вспомогательные методы для обратной совместимости с сервисом
    public UUID artistId() {
        return artist != null ? artist.id() : null;
    }

    public UUID museumId() {
        return museum != null ? museum.id() : null;
    }

    public String photo() {
        return content;
    }
}