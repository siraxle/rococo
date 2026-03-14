package guru.qa.rococouserdata.grpc;

import guru.qa.rococouserdata.model.UserEntity;
import guru.qa.rococouserdata.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import rococo.grpc.userdata.*;

import java.util.UUID;

@GrpcService
public class UserdataGrpcService extends UserdataServiceGrpc.UserdataServiceImplBase {

    private final UserRepository userRepository;

    public UserdataGrpcService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getId()));

            responseObserver.onNext(mapToProtoUser(user));
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format: " + request.getId())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getUserByUsername(GetUserByUsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String username = request.getUsername();
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            responseObserver.onNext(mapToProtoUser(user));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User not found with username: " + request.getUsername())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String username = request.getUsername();

            if (userRepository.existsByUsername(username)) {
                responseObserver.onError(
                        Status.ALREADY_EXISTS
                                .withDescription("User with username '" + username + "' already exists")
                                .asRuntimeException()
                );
                return;
            }

            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
            user.setAvatar(request.getAvatar());

            UserEntity savedUser = userRepository.save(user);

            responseObserver.onNext(mapToProtoUser(savedUser));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to create user: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getId()));

            if (request.hasFirstname()) {
                user.setFirstname(request.getFirstname());
            }
            if (request.hasLastname()) {
                user.setLastname(request.getLastname());
            }
            if (request.hasAvatar()) {
                user.setAvatar(request.getAvatar());
            }

            UserEntity updatedUser = userRepository.save(user);

            responseObserver.onNext(mapToProtoUser(updatedUser));
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format: " + request.getId())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
    }

    private UserResponse mapToProtoUser(UserEntity user) {
        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(user.getId().toString())
                .setUsername(user.getUsername());

        if (user.getFirstname() != null) {
            builder.setFirstname(user.getFirstname());
        }
        if (user.getLastname() != null) {
            builder.setLastname(user.getLastname());
        }
        if (user.getAvatar() != null) {
            builder.setAvatar(user.getAvatar());
        }

        return builder.build();
    }
}