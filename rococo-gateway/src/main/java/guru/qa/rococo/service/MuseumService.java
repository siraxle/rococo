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
public class MuseumService {

    private final MuseumGrpcClient museumGrpcClient;
    private final GeoGrpcClient geoGrpcClient;

    public MuseumService(MuseumGrpcClient museumGrpcClient, GeoGrpcClient geoGrpcClient) {
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
        Map<String, Object> geo = new HashMap<>();
        Map<String, Object> countryMap = new HashMap<>();

        try {
            String countryId = response.getCountryId();
            if (countryId != null && !countryId.isEmpty()) {
                CountryResponse countryResponse = geoGrpcClient.getCountryById(countryId);
                if (countryResponse != null) {
                    countryMap.put("id", countryResponse.getId());
                    countryMap.put("name", countryResponse.getName());

                    geo.put("city", response.getCity());
                    geo.put("country", countryMap);
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
                geo.isEmpty() ? null : geo
        );
    }
}