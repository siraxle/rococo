package guru.qa.rococo.service;

import guru.qa.rococo.grpc.UserdataGrpcClient;
import guru.qa.rococo.model.User;
import org.springframework.stereotype.Service;
import rococo.grpc.userdata.UserResponse;

import java.util.UUID;

@Service
public class UserService {

    private final UserdataGrpcClient userdataGrpcClient;

    public UserService(UserdataGrpcClient userdataGrpcClient) {
        this.userdataGrpcClient = userdataGrpcClient;
    }

    public User getUserById(String id) {
        UserResponse response = userdataGrpcClient.getUserById(id);
        return mapToUser(response);
    }

    public User getUserByUsername(String username) {
        UserResponse response = userdataGrpcClient.getUserByUsername(username);
        return mapToUser(response);
    }

    public User createUser(String username, String firstname, String lastname, String avatar) {
        System.out.println("Creating user with: " + username + ", " + firstname + ", " + lastname);
        try {
            UserResponse response = userdataGrpcClient.createUser(username, firstname, lastname, avatar);
            System.out.println("gRPC response: " + response);
            return mapToUser(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private User mapToUser(UserResponse response) {
        return new User(
                UUID.fromString(response.getId()),
                response.getUsername(),
                response.getFirstname(),
                response.getLastname(),
                response.getAvatar()
        );
    }
}