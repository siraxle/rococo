package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;
import rococo.grpc.artist.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class ArtistGrpcClient {

    private ManagedChannel channel;
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 8091)
                .usePlaintext()
                .build();

        artistStub = ArtistServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public ArtistResponse getArtist(String id) {
        ArtistIdRequest request = ArtistIdRequest.newBuilder()
                .setId(id)
                .build();
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
        ArtistIdRequest request = ArtistIdRequest.newBuilder()
                .setId(id)
                .build();
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