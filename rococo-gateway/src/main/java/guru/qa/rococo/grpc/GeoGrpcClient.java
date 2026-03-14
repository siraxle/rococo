package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;
import rococo.grpc.geo.CountryResponse;
import rococo.grpc.geo.GeoServiceGrpc;
import rococo.grpc.geo.GetCountryByIdRequest;
import rococo.grpc.geo.GetCountryRequest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeoGrpcClient {

    private ManagedChannel channel;
    private GeoServiceGrpc.GeoServiceBlockingStub geoStub;

    @PostConstruct
    public void init() {
        System.out.println("🔨 Initializing Geo gRPC client...");
        channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 8095)
                .usePlaintext()
                .build();

        geoStub = GeoServiceGrpc.newBlockingStub(channel);
        System.out.println("✅ Geo gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public CountryResponse getCountryByCode(String code) {
        GetCountryRequest request = GetCountryRequest.newBuilder()
                .setCode(code)
                .build();
        return geoStub.getCountry(request);
    }

    public CountryResponse getCountryByName(String name) {
        GetCountryRequest request = GetCountryRequest.newBuilder()
                .setName(name)
                .build();
        return geoStub.getCountry(request);
    }

    public List<CountryResponse> getAllCountries() {
        List<CountryResponse> countries = new ArrayList<>();
        geoStub.getAllCountries(rococo.grpc.geo.Empty.newBuilder().build())
                .forEachRemaining(countries::add);
        return countries;
    }

    public CountryResponse getCountryById(String id) {
        GetCountryByIdRequest request = GetCountryByIdRequest.newBuilder()
                .setId(id)
                .build();
        return geoStub.getCountryById(request);
    }
}