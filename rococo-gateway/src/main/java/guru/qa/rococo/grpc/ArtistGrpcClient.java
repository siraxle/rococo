package guru.qa.rococo.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import rococo.grpc.artist.*;

@Component
public class ArtistGrpcClient {

    @GrpcClient("artist-service")
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    public ArtistResponse getArtist(String id) {
        ArtistIdRequest request = ArtistIdRequest.newBuilder()
                .setId(id)
                .build();
        return artistStub.getArtist(request);
    }

    public ArtistResponse createArtist(String name, String biography, String photo) {
        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(name)
                .setBiography(biography)
                .setPhoto(photo)
                .build();
        return artistStub.createArtist(request);
    }

    public ArtistResponse updateArtist(String id, String name, String biography, String photo) {
        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(id)
                .setName(name)
                .setBiography(biography)
                .setPhoto(photo)
                .build();
        return artistStub.updateArtist(request);
    }

    public void deleteArtist(String id) {
        ArtistIdRequest request = ArtistIdRequest.newBuilder()
                .setId(id)
                .build();
        artistStub.deleteArtist(request);
    }

    public ArtistListResponse getAllArtists() {
        return artistStub.getAllArtists(rococo.grpc.artist.Empty.newBuilder().build());
    }
}