package io.student.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import rococo.grpc.userdata.CreateUserRequest;
import rococo.grpc.userdata.UserdataServiceGrpc;

@Component
public class UserdataGrpcClient {

    private ManagedChannel channel;
    private UserdataServiceGrpc.UserdataServiceBlockingStub userdataStub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 8096)
                .usePlaintext()
                .build();

        userdataStub = UserdataServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public void createUser(String username) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUsername(username)
                .setFirstname("")
                .setLastname("")
                .setAvatar("")
                .build();
        userdataStub.createUser(request);
    }
}