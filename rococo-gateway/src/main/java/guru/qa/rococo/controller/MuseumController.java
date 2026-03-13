package guru.qa.rococo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import guru.qa.rococo.model.Museum;
import guru.qa.rococo.service.MuseumService;
import guru.qa.rococo.service.CountryService;
import guru.qa.rococo.entity.CountryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final MuseumService museumService;
    private final CountryService countryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MuseumController(MuseumService museumService, CountryService countryService, ObjectMapper objectMapper) {
        this.museumService = museumService;
        this.countryService = countryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<Page<Map<String, Object>>> getAllMuseums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String city) {

        List<Museum> museums = museumService.getAllMuseums(page, size, title, city);
        List<Map<String, Object>> museumList = museums.stream()
                .map(museum -> {
                    Map<String, Object> museumMap = new HashMap<>();
                    museumMap.put("id", museum.id().toString());
                    museumMap.put("title", museum.title());
                    museumMap.put("description", museum.description() != null ? museum.description() : "");
                    museumMap.put("photo", museum.photo() != null ? museum.photo() : "");

                    Map<String, Object> geoMap = new HashMap<>();
                    geoMap.put("city", museum.city());

                    Map<String, Object> countryMap = new HashMap<>();
                    countryMap.put("id", museum.country() != null ? museum.country().getId().toString() : UUID.randomUUID().toString());
                    countryMap.put("name", museum.country() != null ? museum.country().getName() : "Франция");

                    geoMap.put("country", countryMap);
                    museumMap.put("geo", geoMap);

                    return museumMap;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> museumPage = new PageImpl<>(
                museumList,
                PageRequest.of(page, size),
                museums.size()
        );
        return ResponseEntity.ok(museumPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMuseum(@PathVariable UUID id) {
        Museum museum = museumService.getMuseumById(id);

        Map<String, Object> museumMap = new HashMap<>();
        museumMap.put("id", museum.id().toString());
        museumMap.put("title", museum.title());
        museumMap.put("description", museum.description() != null ? museum.description() : "");
        museumMap.put("photo", museum.photo() != null ? museum.photo() : "");

        Map<String, Object> geoMap = new HashMap<>();
        geoMap.put("city", museum.city());

        Map<String, Object> countryMap = new HashMap<>();
        if (museum.country() != null) {
            countryMap.put("id", museum.country().getId().toString());
            countryMap.put("name", museum.country().getName());
        } else {
            countryMap.put("id", UUID.randomUUID().toString());
            countryMap.put("name", "Неизвестно");
        }

        geoMap.put("country", countryMap);
        museumMap.put("geo", geoMap);

        return ResponseEntity.ok(museumMap);
    }

}