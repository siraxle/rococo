package guru.qa.rococo.controller;

import guru.qa.rococo.entity.MuseumEntity;
import guru.qa.rococo.service.MuseumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final MuseumService museumService;

    @Autowired
    public MuseumController(MuseumService museumService) {
        this.museumService = museumService;
    }

    @GetMapping
    public List<MuseumEntity> getAllMuseums() {
        return museumService.getAllMuseums();
    }

    @GetMapping("/{id}")
    public MuseumEntity getMuseumById(@PathVariable UUID id) {
        return museumService.getMuseumById(id);
    }

    @PostMapping
    public MuseumEntity createMuseum(@RequestBody MuseumEntity museum) {
        return museumService.createMuseum(museum);
    }

    @PatchMapping("/{id}")
    public MuseumEntity updateMuseum(@PathVariable UUID id, @RequestBody MuseumEntity museum) {
        museum.setId(id);
        return museumService.updateMuseum(museum);
    }

    @DeleteMapping("/{id}")
    public void deleteMuseum(@PathVariable UUID id) {
        museumService.deleteMuseum(id);
    }
}