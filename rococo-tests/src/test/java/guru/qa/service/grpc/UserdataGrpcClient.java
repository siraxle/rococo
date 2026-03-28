package guru.qa.service.grpc;

import guru.qa.config.Config;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import rococo.grpc.userdata.CreateUserRequest;
import rococo.grpc.userdata.UpdateUserRequest;
import rococo.grpc.userdata.DeleteUserRequest;
import rococo.grpc.userdata.UserResponse;
import rococo.grpc.userdata.UserdataServiceGrpc;

public class UserdataGrpcClient implements AutoCloseable {

    private final ManagedChannel channel;
    private final UserdataServiceGrpc.UserdataServiceBlockingStub userdataStub;

    public UserdataGrpcClient() {
        Config config = Config.getInstance();
        this.channel = ManagedChannelBuilder.forAddress(
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

    public UserResponse updateUser(String userId, String firstname, String lastname, String avatar) {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setId(userId)
                .setFirstname(firstname != null ? firstname : "")
                .setLastname(lastname != null ? lastname : "")
                .setAvatar(avatar != null ? avatar : "")
                .build();
        return userdataStub.updateUser(request);
    }

    public void deleteUser(String id) {
        DeleteUserRequest request = DeleteUserRequest.newBuilder()
                .setId(id)
                .build();
        userdataStub.deleteUser(request);
    }

    @Override
    public void close() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}