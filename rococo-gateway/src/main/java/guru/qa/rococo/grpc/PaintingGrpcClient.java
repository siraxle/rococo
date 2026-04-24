package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rococo.grpc.painting.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class PaintingGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(PaintingGrpcClient.class);

    private ManagedChannel channel;
    private PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    @Value("${grpc.client.painting-service.address}")
    private String paintingServiceAddress;

    @PostConstruct
    public void init() {
        String host = paintingServiceAddress.replace("static://", "").split(":")[0];
        int port = Integer.parseInt(paintingServiceAddress.replace("static://", "").split(":")[1]);

        LOG.info("Initializing Painting gRPC client with address: {}:{}", host, port);
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        paintingStub = PaintingServiceGrpc.newBlockingStub(channel);
        LOG.info("Painting gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutting down Painting gRPC client...");
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public PaintingResponse getPainting(String id) {
        PaintingIdRequest request = PaintingIdRequest.newBuilder().setId(id).build();
        return paintingStub.getPainting(request);
    }

    public PaintingResponse createPainting(String title, String description, String artistId, String museumId, String photo) {
        CreatePaintingRequest.Builder builder = CreatePaintingRequest.newBuilder()
                .setTitle(title != null ? title : "");

        if (artistId == null || artistId.isBlank()) {
            throw new IllegalArgumentException("Artist ID is required");
        }
        builder.setArtistId(artistId);
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
        UpdatePaintingRequest.Builder builder = UpdatePaintingRequest.newBuilder().setId(id);

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
        PaintingIdRequest request = PaintingIdRequest.newBuilder().setId(id).build();
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