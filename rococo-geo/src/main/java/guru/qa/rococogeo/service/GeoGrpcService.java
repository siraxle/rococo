package guru.qa.rococogeo.service;

import guru.qa.rococogeo.model.CountryEntity;
import guru.qa.rococogeo.repository.CountryRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import rococo.grpc.geo.*;

import java.util.UUID;

@GrpcService
public class GeoGrpcService extends GeoServiceGrpc.GeoServiceImplBase {

    private final CountryRepository countryRepository;

    public GeoGrpcService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getCountry(GetCountryRequest request, StreamObserver<CountryResponse> responseObserver) {
        try {
            CountryEntity country = null;

            if (!request.getCode().isEmpty()) {
                country = countryRepository.findByCode(request.getCode())
                        .orElse(null);
            }

            if (country == null && !request.getName().isEmpty()) {
                country = countryRepository.findByName(request.getName())
                        .orElse(null);
            }

            if (country == null) {
                responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription("Country not found")
                                .asRuntimeException()
                );
                return;
            }

            responseObserver.onNext(mapToProtoCountry(country));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error getting country: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getAllCountries(rococo.grpc.geo.Empty request,
                                StreamObserver<CountryResponse> responseObserver) {
        try {
            countryRepository.findAll().forEach(country -> {
                responseObserver.onNext(mapToProtoCountry(country));
            });
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error fetching countries: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getCountryById(GetCountryByIdRequest request, StreamObserver<CountryResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            CountryEntity country = countryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Country not found with id: " + request.getId()));

            responseObserver.onNext(mapToProtoCountry(country));
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
                            .withDescription("Country not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    private CountryResponse mapToProtoCountry(CountryEntity country) {
        return CountryResponse.newBuilder()
                .setId(country.getId().toString())
                .setName(country.getName())
                .setCode(country.getCode())
                .build();
    }
}