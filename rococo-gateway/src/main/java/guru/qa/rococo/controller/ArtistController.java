package guru.qa.rococo.controller;

import guru.qa.rococo.entity.ArtistEntity;
import guru.qa.rococo.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public List<ArtistEntity> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GetMapping("/{id}")
    public ArtistEntity getArtistById(@PathVariable UUID id) {
        return artistService.getArtistById(id);
    }

    @PostMapping
    public ArtistEntity createArtist(@RequestBody ArtistEntity artist) {
        return artistService.createArtist(artist);
    }

    @PatchMapping("/{id}")
    public ArtistEntity updateArtist(@PathVariable UUID id, @RequestBody ArtistEntity artist) {
        artist.setId(id);
        return artistService.updateArtist(artist);
    }

    @DeleteMapping("/{id}")
    public void deleteArtist(@PathVariable UUID id) {
        artistService.deleteArtist(id);
    }
}