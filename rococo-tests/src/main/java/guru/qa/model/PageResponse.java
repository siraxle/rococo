package guru.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PageResponse<T>(
        @JsonProperty("content") List<T> content,
        @JsonProperty("pageable") Object pageable,
        @JsonProperty("totalElements") long totalElements,
        @JsonProperty("totalPages") int totalPages,
        @JsonProperty("last") boolean last,
        @JsonProperty("size") int size,
        @JsonProperty("number") int number,
        @JsonProperty("sort") Object sort,
        @JsonProperty("first") boolean first,
        @JsonProperty("numberOfElements") int numberOfElements,
        @JsonProperty("empty") boolean empty
) {}
