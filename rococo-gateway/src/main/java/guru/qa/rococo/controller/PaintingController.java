package guru.qa.rococo.controller;

import guru.qa.rococo.model.Painting;
import guru.qa.rococo.service.PaintingService;
import guru.qa.rococo.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {

    private final PaintingService paintingService;
    private final ArtistService artistService;

    @Autowired
    public PaintingController(PaintingService paintingService, ArtistService artistService) {
        this.paintingService = paintingService;
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<Page<Painting>> getAllPaintings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID artistId,
            @RequestParam(required = false) UUID museumId) {

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        List<Painting> paintings = paintingService.getAllPaintings(page, size, title, artistId, museumId);

        // Обогащаем данными о художнике
        List<Painting> enrichedPaintings = paintings.stream()
                .map(this::enrichPaintingWithArtistName)
                .collect(Collectors.toList());

        Page<Painting> paintingPage = new PageImpl<>(
                enrichedPaintings,
                PageRequest.of(page, size),
                enrichedPaintings.size()
        );
        return ResponseEntity.ok(paintingPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Painting> getPaintingById(@PathVariable UUID id) {
        Painting painting = paintingService.getPaintingById(id);
        return ResponseEntity.ok(enrichPaintingWithArtistName(painting));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Painting>> getPaintingsByArtist(
            @PathVariable UUID artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Painting> paintings = paintingService.getAllPaintings(page, size, null, artistId, null);
        List<Painting> enrichedPaintings = paintings.stream()
                .map(this::enrichPaintingWithArtistName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(enrichedPaintings);
    }

    @GetMapping("/author/{artistId}")
    public ResponseEntity<List<Painting>> getPaintingsByAuthor(
            @PathVariable UUID artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return getPaintingsByArtist(artistId, page, size);
    }

    @GetMapping("/museum/{museumId}")
    public ResponseEntity<List<Painting>> getPaintingsByMuseum(
            @PathVariable UUID museumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Painting> paintings = paintingService.getAllPaintings(page, size, null, null, museumId);
        List<Painting> enrichedPaintings = paintings.stream()
                .map(this::enrichPaintingWithArtistName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(enrichedPaintings);
    }

    @PostMapping
    public ResponseEntity<Painting> createPainting(@RequestBody Painting painting) {
        Painting created = paintingService.createPainting(
                painting.title(),
                painting.description(),
                painting.artistId(),  // используем вспомогательный метод
                painting.museumId(),
                painting.photo()      // используем вспомогательный метод
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(enrichPaintingWithArtistName(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Painting> updatePainting(
            @PathVariable UUID id,
            @RequestBody Painting painting) {

        Painting updated = paintingService.updatePainting(
                id,
                painting.title(),
                painting.description(),
                painting.artistId(),
                painting.museumId(),
                painting.photo()
        );
        return ResponseEntity.ok(enrichPaintingWithArtistName(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePainting(@PathVariable UUID id) {
        paintingService.deletePainting(id);
        return ResponseEntity.noContent().build();
    }

    private Painting enrichPaintingWithArtistName(Painting painting) {
        if (painting.artist() == null || painting.artist().id() == null) {
            return painting;
        }

        // Получаем имя художника
        String artistName = artistService.getArtistName(painting.artist().id());

        // Создаем новый объект ArtistInfo с именем
        Painting.ArtistInfo enrichedArtist = new Painting.ArtistInfo(
                painting.artist().id(),
                artistName
        );

        // Возвращаем новую Painting с обогащенным artist
        return new Painting(
                painting.id(),
                painting.title(),
                painting.description(),
                painting.content(),
                enrichedArtist,
                painting.museum()
        );
    }
}