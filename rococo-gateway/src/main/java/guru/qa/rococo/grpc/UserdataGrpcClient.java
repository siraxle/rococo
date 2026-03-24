package guru.qa.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;
import rococo.grpc.userdata.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class UserdataGrpcClient {

    private ManagedChannel channel;
    private UserdataServiceGrpc.UserdataServiceBlockingStub userdataStub;

    @PostConstruct
    public void init() {
        System.out.println("Initializing Userdata gRPC client...");
        channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 8096)
                .usePlaintext()
                .build();

        userdataStub = UserdataServiceGrpc.newBlockingStub(channel);
        System.out.println("Userdata gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down Userdata gRPC client...");
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public UserResponse getUserById(String id) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setId(id)
                .build();
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
        UpdateUserRequest.Builder builder = UpdateUserRequest.newBuilder()
                .setId(id);

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