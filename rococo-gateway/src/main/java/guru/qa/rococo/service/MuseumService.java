package guru.qa.rococo.service;

import guru.qa.rococo.entity.CountryEntity;
import guru.qa.rococo.grpc.MuseumGrpcClient;
import guru.qa.rococo.model.Museum;
import org.springframework.stereotype.Service;
import rococo.grpc.museum.MuseumListResponse;
import rococo.grpc.museum.MuseumResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MuseumService {

    private final MuseumGrpcClient museumGrpcClient;

    public MuseumService(MuseumGrpcClient museumGrpcClient) {
        this.museumGrpcClient = museumGrpcClient;
    }

    public Museum getMuseumById(UUID id) {
        MuseumResponse response = museumGrpcClient.getMuseum(id.toString());
        return mapToMuseum(response);
    }

    public Museum createMuseum(String title, String description, String city, String address, String photo) {
        MuseumResponse response = museumGrpcClient.createMuseum(title, description, city, address, photo);
        return mapToMuseum(response);
    }

    public Museum updateMuseum(UUID id, String title, String description, String city, String address, String photo) {
        MuseumResponse response = museumGrpcClient.updateMuseum(id.toString(), title, description, city, address, photo);
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
        CountryEntity country = determineCountryByCity(response.getCity());

        return new Museum(
                UUID.fromString(response.getId()),
                response.getTitle(),
                response.getDescription(),
                response.getCity(),
                response.getAddress(),
                response.getPhoto(),
                country
        );
    }

    private CountryEntity determineCountryByCity(String city) {
        if (city == null) return null;

        CountryEntity country = new CountryEntity();

        switch (city.toLowerCase()) {
            case "париж":
                country.setName("Франция");
                country.setId(UUID.nameUUIDFromBytes("Франция".getBytes()));
                break;
            case "санкт-петербург":
            case "москва":
                country.setName("Россия");
                country.setId(UUID.nameUUIDFromBytes("Россия".getBytes()));
                break;
            case "флоренция":
            case "рим":
                country.setName("Италия");
                country.setId(UUID.nameUUIDFromBytes("Италия".getBytes()));
                break;
            case "берлин":
                country.setName("Германия");
                country.setId(UUID.nameUUIDFromBytes("Германия".getBytes()));
                break;
            case "мадрид":
                country.setName("Испания");
                country.setId(UUID.nameUUIDFromBytes("Испания".getBytes()));
                break;
            default:
                return null;
        }
        return country;
    }
}