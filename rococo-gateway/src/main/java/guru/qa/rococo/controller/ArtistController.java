package guru.qa.rococo.controller;

import guru.qa.rococo.model.Artist;
import guru.qa.rococo.service.ArtistGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistGatewayService artistService;

    @Autowired
    public ArtistController(ArtistGatewayService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<Page<Artist>> getAllArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) page = 0;
        int actualSize = 100;
        if (actualSize > 100) actualSize = 100;

        List<Artist> artists = artistService.getAllArtists(page, actualSize);
        Page<Artist> artistPage = new PageImpl<>(
                artists,
                PageRequest.of(page, size),
                artists.size()
        );

        return ResponseEntity.ok(artistPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable UUID id) {
        Artist artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
    }

    @PostMapping
    public ResponseEntity<Artist> createArtist(@RequestBody Artist artist) {
        Artist createdArtist = artistService.createArtist(
                artist.name(),
                artist.biography(),
                artist.photo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtist);
    }


    @PatchMapping
    public ResponseEntity<Artist> updateArtist(@RequestBody Artist artist) {
        if (artist.id() == null) {
            return ResponseEntity.badRequest().build();
        }

        Artist updatedArtist = artistService.updateArtist(
                artist.id(),
                artist.name(),
                artist.biography(),
                artist.photo()
        );
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable UUID id) {
        artistService.deleteArtist(String.valueOf(id));
        return ResponseEntity.noContent().build();
    }

}