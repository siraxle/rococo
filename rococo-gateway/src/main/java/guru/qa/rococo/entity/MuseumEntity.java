package guru.qa.rococo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "museum")
@Data
public class MuseumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false, unique = true, length = 255)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "city", length = 255)
    private String city;

    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo;

    @ManyToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id", columnDefinition = "BINARY(16)", nullable = false)
    private CountryEntity country;

    // getters and setters
}