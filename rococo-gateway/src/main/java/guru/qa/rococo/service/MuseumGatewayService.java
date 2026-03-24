package guru.qa.rococo.service;

import guru.qa.rococo.grpc.GeoGrpcClient;
import guru.qa.rococo.grpc.MuseumGrpcClient;
import guru.qa.rococo.model.Museum;
import org.springframework.stereotype.Service;
import rococo.grpc.geo.CountryResponse;
import rococo.grpc.museum.MuseumListResponse;
import rococo.grpc.museum.MuseumResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MuseumGatewayService {

    private final MuseumGrpcClient museumGrpcClient;
    private final GeoGrpcClient geoGrpcClient;

    public MuseumGatewayService(MuseumGrpcClient museumGrpcClient, GeoGrpcClient geoGrpcClient) {
        this.museumGrpcClient = museumGrpcClient;
        this.geoGrpcClient = geoGrpcClient;
    }

    public Museum getMuseumById(UUID id) {
        MuseumResponse response = museumGrpcClient.getMuseum(id.toString());
        return mapToMuseum(response);
    }

    public Museum createMuseum(String title, String description, String city,
                               String address, String photo, String countryId) {
        MuseumResponse response = museumGrpcClient.createMuseum(
                title, description, city, address, photo, countryId);
        return mapToMuseum(response);
    }

    public Museum updateMuseum(UUID id, String title, String description, String city,
                               String address, String photo, String countryId) {
        MuseumResponse response = museumGrpcClient.updateMuseum(
                id.toString(), title, description, city, address, photo, countryId);
        return mapToMuseum(response);
    }

    public void deleteMuseum(UUID id) {
        museumGrpcClient.deleteMuseum(id.toString());
    }

    public List<Museum> getAllMuseums(int page, int size, String title, String city) {
        MuseumListResponse response = museumGrpcClient.getAllMuseums(page, size, title, city);
        return response.getMuseumsList().stream()
                .map(this::mapToMuseum)
                .collect(Collectors.toList());
    }

    private Museum mapToMuseum(MuseumResponse response) {
        Museum.GeoInfo geo = null;

        try {
            String countryId = response.getCountryId();
            if (countryId != null && !countryId.isEmpty()) {
                CountryResponse countryResponse = geoGrpcClient.getCountryById(countryId);
                if (countryResponse != null) {
                    Museum.CountryInfo countryInfo = new Museum.CountryInfo(
                            UUID.fromString(countryResponse.getId()),
                            countryResponse.getName(),
                            countryResponse.getCode()
                    );
                    geo = new Museum.GeoInfo(
                            response.getCity(),
                            countryInfo
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get country from geo service: " + e.getMessage());
        }

        return new Museum(
                UUID.fromString(response.getId()),
                response.getTitle(),
                response.getDescription(),
                response.getCity(),
                response.getAddress(),
                response.getPhoto(),
                geo
        );
    }
}