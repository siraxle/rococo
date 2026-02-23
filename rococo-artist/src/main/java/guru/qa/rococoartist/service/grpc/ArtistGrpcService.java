package guru.qa.rococoartist.service.grpc;

import org.springframework.grpc.server.service.GrpcService;
import rococo.grpc.artist.*;
import guru.qa.rococoartist.model.ArtistEntity;
import guru.qa.rococoartist.service.ArtistService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

@GrpcService
public class ArtistGrpcService extends ArtistServiceGrpc.ArtistServiceImplBase {

    private final ArtistService artistService;

    public ArtistGrpcService(ArtistService artistService) {
        this.artistService = artistService;
    }

    @Override
    public void getArtist(ArtistIdRequest request, StreamObserver<ArtistResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            ArtistEntity artist = artistService.getArtistById(id);
            responseObserver.onNext(mapToProtoArtist(artist));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format: " + request.getId())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Artist not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void createArtist(CreateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        try {
            ArtistEntity artist = artistService.createArtist(
                    request.getName(),
                    request.getBiography(),
                    request.getPhoto()
            );
            responseObserver.onNext(mapToProtoArtist(artist));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to create artist: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void updateArtist(UpdateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            ArtistEntity artist = artistService.updateArtist(
                    id,
                    request.getName(),
                    request.getBiography(),
                    request.getPhoto()
            );
            responseObserver.onNext(mapToProtoArtist(artist));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format: " + request.getId())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Artist not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getAllArtists(GetAllArtistsRequest request, StreamObserver<ArtistListResponse> responseObserver) {
        try {
            int page = request.getPage();
            int size = request.getSize();

            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;

            ArtistListResponse response = artistService.getAllArtists(page, size);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error fetching artists: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void deleteArtist(ArtistIdRequest request, StreamObserver<Empty> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            artistService.deleteArtist(id);
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format: " + request.getId())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Artist not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    private ArtistResponse mapToProtoArtist(ArtistEntity artist) {
        return ArtistResponse.newBuilder()
                .setId(artist.getId().toString())
                .setName(artist.getName())
                .setBiography(artist.getBiography() != null ? artist.getBiography() : "")
                .setPhoto(artist.getPhoto() != null ? artist.getPhoto() : "")
                .build();
    }
}