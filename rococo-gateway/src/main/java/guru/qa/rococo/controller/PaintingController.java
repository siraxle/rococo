package guru.qa.rococo.controller;

import guru.qa.rococo.entity.PaintingEntity;
import guru.qa.rococo.service.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {

    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public List<PaintingEntity> getAllPaintings() {
        return paintingService.getAllPaintings();
    }

    @GetMapping("/{id}")
    public PaintingEntity getPaintingById(@PathVariable UUID id) {
        return paintingService.getPaintingById(id);
    }

    @GetMapping("/artist/{artistId}")
    public List<PaintingEntity> getPaintingsByArtist(@PathVariable UUID artistId) {
        return paintingService.getPaintingsByArtistId(artistId);
    }

    @GetMapping("/museum/{museumId}")
    public List<PaintingEntity> getPaintingsByMuseum(@PathVariable UUID museumId) {
        return paintingService.getPaintingsByMuseumId(museumId);
    }

    @PostMapping
    public PaintingEntity createPainting(@RequestBody PaintingEntity painting) {
        return paintingService.createPainting(painting);
    }

    @PatchMapping("/{id}")
    public PaintingEntity updatePainting(@PathVariable UUID id, @RequestBody PaintingEntity painting) {
        painting.setId(id);
        return paintingService.updatePainting(painting);
    }

    @DeleteMapping("/{id}")
    public void deletePainting(@PathVariable UUID id) {
        paintingService.deletePainting(id);
    }
}