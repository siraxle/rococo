package io.student.rococo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rococo.grpc.userdata.CreateUserRequest;
import rococo.grpc.userdata.UserdataServiceGrpc;

@Component
public class UserdataGrpcClient {

    private ManagedChannel channel;
    private UserdataServiceGrpc.UserdataServiceBlockingStub userdataStub;

    @Value("${grpc.client.userdata-service.address}")
    private String userdataServiceAddress;

    @PostConstruct
    public void init() {
        String host = userdataServiceAddress.replace("static://", "").split(":")[0];
        int port = Integer.parseInt(userdataServiceAddress.replace("static://", "").split(":")[1]);

        channel = ManagedChannelBuilder
                .forAddress(host, port)
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