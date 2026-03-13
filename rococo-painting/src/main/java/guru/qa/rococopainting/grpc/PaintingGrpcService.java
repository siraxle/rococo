package guru.qa.rococopainting.grpc;

import guru.qa.rococopainting.model.PaintingEntity;
import guru.qa.rococopainting.service.PaintingService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.server.service.GrpcService;
import rococo.grpc.painting.*;

import java.util.UUID;

@GrpcService
public class PaintingGrpcService extends PaintingServiceGrpc.PaintingServiceImplBase {

    private final PaintingService paintingService;

    public PaintingGrpcService(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @Override
    public void getPainting(PaintingIdRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            PaintingEntity painting = paintingService.getPaintingById(id);

            PaintingResponse response = mapToProto(painting);

            responseObserver.onNext(response);
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
                            .withDescription("Painting not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void createPainting(CreatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            UUID artistId = UUID.fromString(request.getArtistId());
            UUID museumId = request.hasMuseumId() ? UUID.fromString(request.getMuseumId()) : null;

            PaintingEntity painting = paintingService.createPainting(
                    request.getTitle(),
                    request.getDescription(),
                    artistId,
                    museumId,
                    request.hasPhoto() ? request.getPhoto() : null
            );

            PaintingResponse response = mapToProto(painting);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format: " + e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to create painting: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void updatePainting(UpdatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());

            String title = request.hasTitle() ? request.getTitle() : null;
            String description = request.hasDescription() ? request.getDescription() : null;
            UUID artistId = request.hasArtistId() ? UUID.fromString(request.getArtistId()) : null;
            UUID museumId = request.hasMuseumId() ? UUID.fromString(request.getMuseumId()) : null;
            String photo = request.hasPhoto() ? request.getPhoto() : null;

            PaintingEntity painting = paintingService.updatePainting(
                    id,
                    title,
                    description,
                    artistId,
                    museumId,
                    photo
            );

            PaintingResponse response = mapToProto(painting);

            responseObserver.onNext(response);
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
                            .withDescription("Painting not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }



    @Override
    public void getAllPaintings(GetAllPaintingsRequest request, StreamObserver<PaintingListResponse> responseObserver) {
        try {
            int page = request.getPage();
            int size = request.getSize();

            String title = request.hasTitle() ? request.getTitle() : null;
            UUID artistId = request.hasArtistId() ? UUID.fromString(request.getArtistId()) : null;
            UUID museumId = request.hasMuseumId() ? UUID.fromString(request.getMuseumId()) : null;

            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;

            Pageable pageable = PageRequest.of(page, size);
            Page<PaintingEntity> paintingPage = paintingService.getAllPaintings(title, artistId, museumId, pageable);

            PaintingListResponse.Builder responseBuilder = PaintingListResponse.newBuilder()
                    .setTotalPages(paintingPage.getTotalPages())
                    .setTotalElements((int) paintingPage.getTotalElements())
                    .setCurrentPage(paintingPage.getNumber());

            paintingPage.getContent().forEach(entity ->
                    responseBuilder.addPaintings(mapToProto(entity))
            );

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format in filters: " + e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error fetching paintings: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void deletePainting(PaintingIdRequest request, StreamObserver<Empty> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            paintingService.deletePainting(id);
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
                            .withDescription("Painting not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    private PaintingResponse mapToProto(PaintingEntity entity) {
        PaintingResponse.Builder builder = PaintingResponse.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setArtistId(entity.getArtistId().toString());

        if (entity.getDescription() != null) {
            builder.setDescription(entity.getDescription());
        }
        if (entity.getMuseumId() != null) {
            builder.setMuseumId(entity.getMuseumId().toString());
        }
        if (entity.getPhoto() != null) {
            builder.setPhoto(entity.getPhoto());
        }

        return builder.build();
    }
}