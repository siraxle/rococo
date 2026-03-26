package guru.qa.rococo.controller;

import guru.qa.rococo.model.Museum;
import guru.qa.rococo.service.MuseumGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final MuseumGatewayService museumService;

    @Autowired
    public MuseumController(MuseumGatewayService museumService) {
        this.museumService = museumService;
    }

    @GetMapping
    public ResponseEntity<Page<Museum>> getAllMuseums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String city) {

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        List<Museum> museums = museumService.getAllMuseums(page, size, title, city);

        Page<Museum> museumPage = new PageImpl<>(
                museums,
                PageRequest.of(page, size),
                museums.size()
        );
        return ResponseEntity.ok(museumPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Museum> getMuseum(@PathVariable UUID id) {
        Museum museum = museumService.getMuseumById(id);
        return ResponseEntity.ok(museum);
    }

    @PostMapping
    public ResponseEntity<Museum> createMuseum(@RequestBody Museum museum) {
        String city = null;
        if (museum.geo() != null) {
            city = museum.geo().city();
        }

        String countryId = null;
        if (museum.geo() != null && museum.geo().country() != null) {
            countryId = museum.geo().country().id() != null ?
                    museum.geo().country().id().toString() : null;
        }

        Museum created = museumService.createMuseum(
                museum.title(),
                museum.description(),
                city,
                museum.address(),
                museum.photo(),
                countryId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping
    public ResponseEntity<Museum> updateMuseum(@RequestBody Museum museum) {
        if (museum.id() == null) {
            return ResponseEntity.badRequest().build();
        }

        String city = null;
        if (museum.geo() != null) {
            city = museum.geo().city();
        }

        String countryId = null;
        if (museum.geo() != null && museum.geo().country() != null) {
            countryId = museum.geo().country().id() != null ?
                    museum.geo().country().id().toString() : null;
        }

        Museum updated = museumService.updateMuseum(
                museum.id(),
                museum.title(),
                museum.description(),
                city,
                museum.address(),
                museum.photo(),
                countryId
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMuseum(@PathVariable UUID id) {
        museumService.deleteMuseum(id);
        return ResponseEntity.noContent().build();
    }
}