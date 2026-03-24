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
        // Извлекаем countryId из geo объекта
        String countryId = null;
        Map<String, Object> geo = museum.geo();
        if (geo != null && geo.containsKey("country")) {
            Map<String, Object> country = (Map<String, Object>) geo.get("country");
            if (country != null && country.containsKey("id")) {
                Object id = country.get("id");
                countryId = id != null ? id.toString() : null;
            }
        }

        Museum created = museumService.createMuseum(
                museum.title(),
                museum.description(),
                museum.city(),
                museum.address(),
                museum.photo(),
                countryId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Museum> updateMuseum(
            @PathVariable UUID id,
            @RequestBody Museum museum) {

        // Извлекаем countryId из geo объекта
        String countryId = null;
        Map<String, Object> geo = museum.geo();
        if (geo != null && geo.containsKey("country")) {
            Map<String, Object> country = (Map<String, Object>) geo.get("country");
            if (country != null && country.containsKey("id")) {
                Object idObj = country.get("id");
                countryId = idObj != null ? idObj.toString() : null;
            }
        }

        Museum updated = museumService.updateMuseum(
                id,
                museum.title(),
                museum.description(),
                museum.city(),
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