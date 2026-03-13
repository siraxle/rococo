package guru.qa.rococo.controller;

import guru.qa.rococo.model.Museum;
import guru.qa.rococo.service.MuseumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final MuseumService museumService;

    public MuseumController(MuseumService museumService) {
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
        // TODO: когда музейный сервис начнёт возвращать totalElements, использовать реальные значения
        Page<Museum> museumPage = new PageImpl<>(museums, PageRequest.of(page, size), museums.size());
        return ResponseEntity.ok(museumPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Museum> getMuseum(@PathVariable UUID id) {
        Museum museum = museumService.getMuseumById(id);
        return ResponseEntity.ok(museum);
    }

    @PostMapping
    public ResponseEntity<Museum> createMuseum(@RequestBody Museum museum) {
        Museum created = museumService.createMuseum(
                museum.title(),
                museum.description(),
                museum.city(),
                museum.address(),
                museum.photo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Museum> updateMuseum(@PathVariable UUID id, @RequestBody Museum museum) {
        Museum updated = museumService.updateMuseum(
                id,
                museum.title(),
                museum.description(),
                museum.city(),
                museum.address(),
                museum.photo()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMuseum(@PathVariable UUID id) {
        museumService.deleteMuseum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/diagnostic/grpc")
    public Map<String, Object> diagnosticGrpc() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("timestamp", new Date().toString());

        // Проверка порта
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", 8092), 1000);
            result.put("portCheck", "✅ Порт 8092 открыт");
        } catch (Exception e) {
            result.put("portCheck", "❌ Ошибка: " + e.getMessage());
        }

        // Проверка gRPC вызова
        try {
            var response = museumService.getAllMuseums(0, 1, null, null);
            result.put("grpcCallSuccess", true);
            result.put("museumsCount", response.size());
        } catch (Exception e) {
            result.put("grpcCallSuccess", false);
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            result.put("stackTrace", e.getStackTrace()[0].toString());
        }

        return result;
    }
}