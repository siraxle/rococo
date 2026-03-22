package guru.qa.rococo.service;

import guru.qa.rococo.grpc.PaintingGrpcClient;
import guru.qa.rococo.model.Painting;
import org.springframework.stereotype.Service;
import rococo.grpc.painting.PaintingListResponse;
import rococo.grpc.painting.PaintingResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaintingService {

    private final PaintingGrpcClient paintingGrpcClient;

    public PaintingService(PaintingGrpcClient paintingGrpcClient) {
        this.paintingGrpcClient = paintingGrpcClient;
    }

    public Painting getPaintingById(UUID id) {
        PaintingResponse response = paintingGrpcClient.getPainting(id.toString());
        return mapToPainting(response);
    }

    public Painting createPainting(String title, String description, String artistId, String museumId, String photo) {
        if (artistId == null || artistId.isBlank()) {
            throw new IllegalArgumentException("Artist ID is required");
        }
        PaintingResponse response = paintingGrpcClient.createPainting(
                title, description, artistId, museumId, photo
        );
        return mapToPainting(response);
    }

    public Painting updatePainting(UUID id, String title, String description, UUID artistId, UUID museumId, String photo) {
        PaintingResponse response = paintingGrpcClient.updatePainting(
                id.toString(),
                title,
                description,
                artistId != null ? artistId.toString() : null,
                museumId != null ? museumId.toString() : null,
                photo
        );
        return mapToPainting(response);
    }

    public void deletePainting(UUID id) {
        paintingGrpcClient.deletePainting(id.toString());
    }

    public List<Painting> getAllPaintings(int page, int size, String title, UUID artistId, UUID museumId) {
        PaintingListResponse response = paintingGrpcClient.getAllPaintings(
                page,
                size,
                title,
                artistId != null ? artistId.toString() : null,
                museumId != null ? museumId.toString() : null
        );
        return response.getPaintingsList().stream()
                .map(this::mapToPainting)
                .collect(Collectors.toList());
    }

    private Painting mapToPainting(PaintingResponse response) {
        Painting.ArtistInfo artistInfo = new Painting.ArtistInfo(
                UUID.fromString(response.getArtistId()),
                null
        );

        Painting.MuseumInfo museumInfo = null;
        if (response.hasMuseumId() && response.getMuseumId() != null && !response.getMuseumId().isEmpty()) {
            museumInfo = new Painting.MuseumInfo(
                    UUID.fromString(response.getMuseumId())
            );
        }

        return new Painting(
                UUID.fromString(response.getId()),
                response.getTitle(),
                response.getDescription(),
                response.hasPhoto() ? response.getPhoto() : null,
                artistInfo,
                museumInfo
        );
    }
}