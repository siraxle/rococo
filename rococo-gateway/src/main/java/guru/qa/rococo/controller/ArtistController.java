package guru.qa.rococo.controller;

import guru.qa.rococo.model.Artist;
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
    public List<Artist> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GetMapping("/{id}")
    public Artist getArtistById(@PathVariable UUID id) {
        return artistService.getArtistById(id);
    }

    @PostMapping
    public Artist createArtist(@RequestBody Artist artist) {
        return artistService.createArtist(artist.name(), artist.biography(), artist.photo());
    }

    @PatchMapping("/{id}")
    public Artist updateArtist(@PathVariable UUID id, @RequestBody Artist artist) {
        return artistService.updateArtist(id, artist.name(), artist.biography(), artist.photo());
    }

    @DeleteMapping("/{id}")
    public void deleteArtist(@PathVariable UUID id) {
        artistService.deleteArtist(String.valueOf(id));
    }
}