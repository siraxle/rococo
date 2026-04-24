package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rococo.grpc.museum.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class MuseumGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(MuseumGrpcClient.class);

    private ManagedChannel channel;
    private MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    @Value("${grpc.client.museum-service.address}")
    private String museumServiceAddress;

    @PostConstruct
    public void init() {
        String host = museumServiceAddress.replace("static://", "").split(":")[0];
        int port = Integer.parseInt(museumServiceAddress.replace("static://", "").split(":")[1]);

        LOG.info("Initializing Museum gRPC client with address: {}:{}", host, port);
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        museumStub = MuseumServiceGrpc.newBlockingStub(channel);
        LOG.info("Museum gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutting down Museum gRPC client...");
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public MuseumResponse getMuseum(String id) {
        MuseumIdRequest request = MuseumIdRequest.newBuilder().setId(id).build();
        return museumStub.getMuseum(request);
    }

    public MuseumResponse createMuseum(String title, String description, String city,
                                       String address, String photo, String countryId) {
        CreateMuseumRequest.Builder builder = CreateMuseumRequest.newBuilder()
                .setTitle(title != null ? title : "")
                .setCity(city != null ? city : "")
                .setAddress(address != null ? address : "");

        if (description != null) {
            builder.setDescription(description);
        }
        if (photo != null) {
            builder.setPhoto(photo);
        }
        if (countryId != null && !countryId.isBlank()) {
            builder.setCountryId(countryId);
        }

        return museumStub.createMuseum(builder.build());
    }

    public MuseumResponse updateMuseum(String id, String title, String description,
                                       String city, String address, String photo, String countryId) {
        UpdateMuseumRequest.Builder builder = UpdateMuseumRequest.newBuilder().setId(id);

        if (title != null && !title.isBlank()) {
            builder.setTitle(title);
        }
        if (description != null) {
            builder.setDescription(description);
        }
        if (city != null && !city.isBlank()) {
            builder.setCity(city);
        }
        if (address != null && !address.isBlank()) {
            builder.setAddress(address);
        }
        if (photo != null) {
            builder.setPhoto(photo);
        }
        if (countryId != null && !countryId.isBlank()) {
            builder.setCountryId(countryId);
        }

        return museumStub.updateMuseum(builder.build());
    }

    public void deleteMuseum(String id) {
        MuseumIdRequest request = MuseumIdRequest.newBuilder().setId(id).build();
        museumStub.deleteMuseum(request);
    }

    public MuseumListResponse getAllMuseums(int page, int size, String title, String city) {
        GetAllMuseumsRequest.Builder builder = GetAllMuseumsRequest.newBuilder()
                .setPage(page)
                .setSize(size);

        if (title != null && !title.isBlank()) {
            builder.setTitle(title);
        }
        if (city != null && !city.isBlank()) {
            builder.setCity(city);
        }

        return museumStub.getAllMuseums(builder.build());
    }
}