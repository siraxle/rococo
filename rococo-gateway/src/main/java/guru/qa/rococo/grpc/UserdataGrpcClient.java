package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rococo.grpc.userdata.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class UserdataGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(UserdataGrpcClient.class);

    private ManagedChannel channel;
    private UserdataServiceGrpc.UserdataServiceBlockingStub userdataStub;

    @Value("${grpc.client.userdata-service.address}")
    private String userdataServiceAddress;

    @PostConstruct
    public void init() {
        String host = userdataServiceAddress.replace("static://", "").split(":")[0];
        int port = Integer.parseInt(userdataServiceAddress.replace("static://", "").split(":")[1]);

        LOG.info("Initializing Userdata gRPC client with address: {}:{}", host, port);
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        userdataStub = UserdataServiceGrpc.newBlockingStub(channel);
        LOG.info("Userdata gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutting down Userdata gRPC client...");
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public UserResponse getUserById(String id) {
        GetUserRequest request = GetUserRequest.newBuilder().setId(id).build();
        return userdataStub.getUser(request);
    }

    public UserResponse getUserByUsername(String username) {
        GetUserByUsernameRequest request = GetUserByUsernameRequest.newBuilder()
                .setUsername(username)
                .build();
        return userdataStub.getUserByUsername(request);
    }

    public UserResponse createUser(String username, String firstname, String lastname, String avatar) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUsername(username)
                .setFirstname(firstname != null ? firstname : "")
                .setLastname(lastname != null ? lastname : "")
                .setAvatar(avatar != null ? avatar : "")
                .build();
        return userdataStub.createUser(request);
    }

    public UserResponse updateUser(String id, String firstname, String lastname, String avatar) {
        UpdateUserRequest.Builder builder = UpdateUserRequest.newBuilder().setId(id);

        if (firstname != null && !firstname.isBlank()) {
            builder.setFirstname(firstname);
        }
        if (lastname != null && !lastname.isBlank()) {
            builder.setLastname(lastname);
        }
        if (avatar != null && !avatar.isBlank()) {
            builder.setAvatar(avatar);
        }

        return userdataStub.updateUser(builder.build());
    }
}