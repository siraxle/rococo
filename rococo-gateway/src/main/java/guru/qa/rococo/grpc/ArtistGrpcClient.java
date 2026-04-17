package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rococo.grpc.artist.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class ArtistGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArtistGrpcClient.class);

    private ManagedChannel channel;
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    @Value("${grpc.client.artist-service.address}")
    private String artistServiceAddress;

    @PostConstruct
    public void init() {
        String host = artistServiceAddress.replace("static://", "").split(":")[0];
        int port = Integer.parseInt(artistServiceAddress.replace("static://", "").split(":")[1]);

        LOG.info("Initializing Artist gRPC client with address: {}:{}", host, port);
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        artistStub = ArtistServiceGrpc.newBlockingStub(channel);
        LOG.info("Artist gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public ArtistResponse getArtist(String id) {
        ArtistIdRequest request = ArtistIdRequest.newBuilder().setId(id).build();
        return artistStub.getArtist(request);
    }

    public ArtistResponse createArtist(String name, String biography, String photo) {
        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(name != null ? name : "")
                .setBiography(biography != null ? biography : "")
                .setPhoto(photo != null ? photo : "")
                .build();
        return artistStub.createArtist(request);
    }

    public ArtistResponse updateArtist(String id, String name, String biography, String photo) {
        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(id)
                .setName(name != null ? name : "")
                .setBiography(biography != null ? biography : "")
                .setPhoto(photo != null ? photo : "")
                .build();
        return artistStub.updateArtist(request);
    }

    public void deleteArtist(String id) {
        ArtistIdRequest request = ArtistIdRequest.newBuilder().setId(id).build();
        artistStub.deleteArtist(request);
    }

    public ArtistListResponse getAllArtists(int page, int size) {
        GetAllArtistsRequest request = GetAllArtistsRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build();
        return artistStub.getAllArtists(request);
    }
}