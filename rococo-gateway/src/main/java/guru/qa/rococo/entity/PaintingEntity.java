package guru.qa.rococo.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "painting")
public class PaintingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Lob
    @Column(name = "content", columnDefinition = "LONGBLOB")
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id", columnDefinition = "BINARY(16)", nullable = false)
    private ArtistEntity artist;

    @ManyToOne
    @JoinColumn(name = "museum_id", referencedColumnName = "id", columnDefinition = "BINARY(16)")
    private MuseumEntity museum;

    // getters and setters
}