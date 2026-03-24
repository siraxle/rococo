package guru.qa.rococo.service;

import guru.qa.rococo.grpc.GeoGrpcClient;
import guru.qa.rococo.model.Country;
import org.springframework.stereotype.Service;
import rococo.grpc.geo.CountryResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CountryGatewayService {

    private final GeoGrpcClient geoGrpcClient;

    public CountryGatewayService(GeoGrpcClient geoGrpcClient) {
        this.geoGrpcClient = geoGrpcClient;
    }

    public List<Country> getAllCountries() {
        return geoGrpcClient.getAllCountries().stream()
                .map(this::mapToCountry)
                .collect(Collectors.toList());
    }

    public Country getCountryById(String id) {
        CountryResponse response = geoGrpcClient.getCountryById(id);
        return mapToCountry(response);
    }

    public Country getCountryByName(String name) {
        CountryResponse response = geoGrpcClient.getCountryByName(name);
        return mapToCountry(response);
    }

    private Country mapToCountry(CountryResponse response) {
        return new Country(
                UUID.fromString(response.getId()),
                response.getName(),
                response.getCode()
        );
    }
}