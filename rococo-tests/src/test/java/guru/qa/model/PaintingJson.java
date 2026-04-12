package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaintingJson(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("photo") String photo,
        @JsonProperty("content") String content,
        @JsonProperty("artist") ArtistInfo artist,
        @JsonProperty("museum") MuseumInfo museum
) {
    public record ArtistInfo(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name
    ) {}

    public record MuseumInfo(
            @JsonProperty("id") String id
    ) {}

    public String artistId() {
        return artist != null ? artist.id() : null;
    }

    public String museumId() {
        return museum != null ? museum.id() : null;
    }
}