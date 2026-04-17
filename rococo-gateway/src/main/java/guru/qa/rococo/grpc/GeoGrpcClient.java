package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rococo.grpc.geo.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeoGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(GeoGrpcClient.class);

    private ManagedChannel channel;
    private GeoServiceGrpc.GeoServiceBlockingStub geoStub;

    @Value("${grpc.client.geo-service.address}")
    private String geoServiceAddress;

    @PostConstruct
    public void init() {
        String host = geoServiceAddress.replace("static://", "").split(":")[0];
        int port = Integer.parseInt(geoServiceAddress.replace("static://", "").split(":")[1]);

        LOG.info("Initializing Geo gRPC client with address: {}:{}", host, port);
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        geoStub = GeoServiceGrpc.newBlockingStub(channel);
        LOG.info("Geo gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public CountryResponse getCountryByCode(String code) {
        GetCountryRequest request = GetCountryRequest.newBuilder().setCode(code).build();
        return geoStub.getCountry(request);
    }

    public CountryResponse getCountryByName(String name) {
        GetCountryRequest request = GetCountryRequest.newBuilder().setName(name).build();
        return geoStub.getCountry(request);
    }

    public List<CountryResponse> getAllCountries() {
        List<CountryResponse> countries = new ArrayList<>();
        geoStub.getAllCountries(rococo.grpc.geo.Empty.newBuilder().build())
                .forEachRemaining(countries::add);
        return countries;
    }

    public CountryResponse getCountryById(String id) {
        GetCountryByIdRequest request = GetCountryByIdRequest.newBuilder().setId(id).build();
        return geoStub.getCountryById(request);
    }
}