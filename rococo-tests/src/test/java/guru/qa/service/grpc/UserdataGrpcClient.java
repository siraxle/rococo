package guru.qa.service.grpc;

import guru.qa.config.Config;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import rococo.grpc.userdata.CreateUserRequest;
import rococo.grpc.userdata.UserResponse;
import rococo.grpc.userdata.UserdataServiceGrpc;

public class UserdataGrpcClient {

    private final UserdataServiceGrpc.UserdataServiceBlockingStub userdataStub;

    public UserdataGrpcClient() {
        Config config = Config.getInstance();
        ManagedChannel channel = ManagedChannelBuilder.forAddress(
                        config.userdataGrpcHost(),
                        config.userdataGrpcPort())
                .usePlaintext()
                .build();
        this.userdataStub = UserdataServiceGrpc.newBlockingStub(channel);
    }

    public UserResponse createUser(String username) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUsername(username)
                .setFirstname("")
                .setLastname("")
                .setAvatar("")
                .build();
        return userdataStub.createUser(request);
    }
}