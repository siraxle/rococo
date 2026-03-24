package guru.qa.rococo.controller;

import guru.qa.rococo.model.Artist;
import guru.qa.rococo.service.ArtistGatewayService;
import guru.qa.rococo.grpc.ArtistGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistGatewayService artistService;
    private final ArtistGrpcClient artistGrpcClient;

    @Autowired
    public ArtistController(ArtistGatewayService artistService, ArtistGrpcClient artistGrpcClient) {
        this.artistService = artistService;
        this.artistGrpcClient = artistGrpcClient;
    }

    @GetMapping
    public ResponseEntity<Page<Artist>> getAllArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;

        List<Artist> artists = artistService.getAllArtists(page, size);
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

    @PatchMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(
            @PathVariable UUID id,
            @RequestBody Artist artist) {

        Artist updatedArtist = artistService.updateArtist(
                id,
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

    @GetMapping("/diagnostic")
    public Map<String, Object> diagnostic() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("timestamp", new Date().toString());
        result.put("service", "gateway");
        result.put("status", "UP");

        result.put("artistServicePortCheck", checkPort(8091));

        Map<String, String> config = new HashMap<>();
        config.put("grpcAddress", "localhost:8091");
        config.put("negotiationType", "plaintext");
        result.put("grpcConfig", config);

        return result;
    }

    @GetMapping("/diagnostic/grpc")
    public Map<String, Object> diagnosticGrpc() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("timestamp", new Date().toString());
        result.put("artistServicePort", 8091);

        // Проверка 1: Пинг порта
        result.put("portCheck", checkPort(8091));

        // Проверка 2: Попытка gRPC вызова
        try {
            result.put("grpcCallStart", new Date().toString());
            var response = artistGrpcClient.getAllArtists(0, 1);
            result.put("grpcCallSuccess", true);
            result.put("artistsCount", response.getArtistsCount());
            result.put("totalElements", response.getTotalElements());
            result.put("totalPages", response.getTotalPages());
            result.put("currentPage", response.getCurrentPage());
        } catch (Exception e) {
            result.put("grpcCallSuccess", false);
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());

            // Получаем причину ошибки
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            result.put("stackTraceFirstLine", stackTrace.split("\n")[0]);

            // Проверяем, есть ли в ошибке "artist-service"
            if (e.getMessage() != null && e.getMessage().contains("artist-service")) {
                result.put("likelyCause", "Проблема с DNS - gateway ищет 'artist-service', а нужно 'localhost'");
            }
        }

        return result;
    }

    private String checkPort(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port), 1000);
            return "✅ Порт " + port + " открыт (TCP соединение успешно)";
        } catch (Exception e) {
            return "❌ Порт " + port + " не отвечает: " + e.getMessage();
        }
    }
}