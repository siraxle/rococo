package guru.qa.rococomuseum.grpc;

import guru.qa.rococomuseum.model.MuseumEntity;
import guru.qa.rococomuseum.service.MuseumService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.server.service.GrpcService;
import rococo.grpc.museum.*;

import java.util.UUID;

@GrpcService
public class MuseumGrpcService extends MuseumServiceGrpc.MuseumServiceImplBase {

    private final MuseumService museumService;

    public MuseumGrpcService(MuseumService museumService) {
        this.museumService = museumService;
    }

    @Override
    public void getMuseum(MuseumIdRequest request, StreamObserver<MuseumResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            MuseumEntity museum = museumService.getMuseumById(id);

            responseObserver.onNext(mapToResponse(museum));
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
                            .withDescription("Museum not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void createMuseum(CreateMuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        try {
            UUID countryId = null;
            if (request.hasCountryId() && !request.getCountryId().isEmpty()) {
                countryId = UUID.fromString(request.getCountryId());
            }

            MuseumEntity museum = museumService.createMuseum(
                    request.getTitle(),
                    request.getDescription(),
                    request.getCity(),
                    request.getAddress(),
                    request.getPhoto(),
                    countryId
            );

            responseObserver.onNext(mapToResponse(museum));
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid request data: " + e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to create museum: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void updateMuseum(UpdateMuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            UUID countryId = request.hasCountryId() ? UUID.fromString(request.getCountryId()) : null;

            MuseumEntity museum = museumService.updateMuseum(
                    id,
                    request.getTitle(),
                    request.getDescription(),
                    request.getCity(),
                    request.getAddress(),
                    request.getPhoto(),
                    countryId
            );

            responseObserver.onNext(mapToResponse(museum));
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
                            .withDescription("Museum not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getAllMuseums(GetAllMuseumsRequest request, StreamObserver<MuseumListResponse> responseObserver) {
        try {
            int page = request.getPage();
            int size = request.getSize();
            String title = request.hasTitle() ? request.getTitle() : null;
            String city = request.hasCity() ? request.getCity() : null;

            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;

            Pageable pageable = PageRequest.of(page, size);
            Page<MuseumEntity> museumPage = museumService.getAllMuseums(title, city, pageable);

            MuseumListResponse.Builder responseBuilder = MuseumListResponse.newBuilder()
                    .setTotalPages(museumPage.getTotalPages())
                    .setTotalElements((int) museumPage.getTotalElements())
                    .setCurrentPage(museumPage.getNumber());

            museumPage.getContent().forEach(entity ->
                    responseBuilder.addMuseums(mapToResponse(entity))
            );

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error fetching museums: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void deleteMuseum(MuseumIdRequest request, StreamObserver<Empty> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            museumService.deleteMuseum(id);
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
                            .withDescription("Museum not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    private MuseumResponse mapToResponse(MuseumEntity entity) {
        return MuseumResponse.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription() != null ? entity.getDescription() : "")
                .setCity(entity.getCity())
                .setAddress(entity.getAddress())
                .setPhoto(entity.getPhoto() != null ? entity.getPhoto() : "")
                .setCountryId(entity.getCountryId() != null ? entity.getCountryId().toString() : "")
                .build();
    }
}