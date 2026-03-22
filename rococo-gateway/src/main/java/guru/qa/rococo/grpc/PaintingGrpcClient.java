package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;
import rococo.grpc.painting.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class PaintingGrpcClient {

    private ManagedChannel channel;
    private PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    @PostConstruct
    public void init() {
        System.out.println("Initializing Painting gRPC client...");
        channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 8093)
                .usePlaintext()
                .build();

        paintingStub = PaintingServiceGrpc.newBlockingStub(channel);
        System.out.println("Painting gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down Painting gRPC client...");
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public PaintingResponse getPainting(String id) {
        PaintingIdRequest request = PaintingIdRequest.newBuilder()
                .setId(id)
                .build();
        return paintingStub.getPainting(request);
    }

    public PaintingResponse createPainting(String title, String description, String artistId, String museumId, String photo) {
        CreatePaintingRequest.Builder builder = CreatePaintingRequest.newBuilder()
                .setTitle(title != null ? title : "");

        if (artistId != null && !artistId.isBlank()) {
            builder.setArtistId(artistId);
        } else {
            throw new IllegalArgumentException("Artist ID is required");
        }
        if (description != null) {
            builder.setDescription(description);
        }
        if (museumId != null && !museumId.isEmpty()) {
            builder.setMuseumId(museumId);
        }
        if (photo != null) {
            builder.setPhoto(photo);
        }
        return paintingStub.createPainting(builder.build());
    }

    public PaintingResponse updatePainting(String id, String title, String description, String artistId, String museumId, String photo) {
        UpdatePaintingRequest.Builder builder = UpdatePaintingRequest.newBuilder()
                .setId(id);

        if (title != null && !title.isBlank()) {
            builder.setTitle(title);
        }
        if (description != null) {
            builder.setDescription(description);
        }
        if (artistId != null && !artistId.isBlank()) {
            builder.setArtistId(artistId);
        }
        if (museumId != null && !museumId.isEmpty()) {
            builder.setMuseumId(museumId);
        }
        if (photo != null) {
            builder.setPhoto(photo);
        }

        return paintingStub.updatePainting(builder.build());
    }

    public void deletePainting(String id) {
        PaintingIdRequest request = PaintingIdRequest.newBuilder()
                .setId(id)
                .build();
        paintingStub.deletePainting(request);
    }

    public PaintingListResponse getAllPaintings(int page, int size, String title, String artistId, String museumId) {
        GetAllPaintingsRequest.Builder builder = GetAllPaintingsRequest.newBuilder()
                .setPage(page)
                .setSize(size);

        if (title != null && !title.isBlank()) {
            builder.setTitle(title);
        }
        if (artistId != null && !artistId.isBlank()) {
            builder.setArtistId(artistId);
        }
        if (museumId != null && !museumId.isEmpty()) {
            builder.setMuseumId(museumId);
        }

        return paintingStub.getAllPaintings(builder.build());
    }
}