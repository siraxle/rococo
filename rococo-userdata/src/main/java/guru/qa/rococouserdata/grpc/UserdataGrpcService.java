package guru.qa.rococouserdata.grpc;

import guru.qa.rococouserdata.model.UserEntity;
import guru.qa.rococouserdata.repository.UserRepository;
import guru.qa.rococouserdata.service.SmallPhoto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;
import rococo.grpc.userdata.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@GrpcService
public class UserdataGrpcService extends UserdataServiceGrpc.UserdataServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(UserdataGrpcService.class);
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

            String avatar = request.getAvatar();
            if (avatar != null && !avatar.isBlank()) {
                LOG.info("Creating user with avatar length: {}", avatar.length());
                try {
                    SmallPhoto smallPhoto = new SmallPhoto(300, 300, "jpg", avatar);
                    byte[] avatarBytes = smallPhoto.bytes();
                    if (avatarBytes != null) {
                        String processedAvatar = new String(avatarBytes, StandardCharsets.UTF_8);
                        LOG.info("Processed avatar length: {}", processedAvatar.length());
                        user.setAvatar(processedAvatar);
                    } else {
                        LOG.warn("SmallPhoto.bytes() returned null, using original avatar");
                        user.setAvatar(avatar);
                    }
                } catch (Exception e) {
                    LOG.error("Failed to process avatar, using original", e);
                    user.setAvatar(avatar);
                }
            } else {
                user.setAvatar(avatar);
            }

            UserEntity savedUser = userRepository.save(user);
            LOG.info("User created with id: {}", savedUser.getId());

            responseObserver.onNext(mapToProtoUser(savedUser));
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Failed to create user", e);
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
                String avatar = request.getAvatar();
                if (avatar != null && !avatar.isBlank()) {
                    LOG.info("Updating user avatar, length: {}", avatar.length());
                    try {
                        SmallPhoto smallPhoto = new SmallPhoto(300, 300, "jpg", avatar);
                        byte[] avatarBytes = smallPhoto.bytes();
                        if (avatarBytes != null) {
                            String processedAvatar = new String(avatarBytes, StandardCharsets.UTF_8);
                            LOG.info("Processed avatar length: {}", processedAvatar.length());
                            user.setAvatar(processedAvatar);
                        } else {
                            LOG.warn("SmallPhoto.bytes() returned null, using original avatar");
                            user.setAvatar(avatar);
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to process avatar for update, using original", e);
                        user.setAvatar(avatar);
                    }
                } else {
                    user.setAvatar(avatar);
                }
            }

            UserEntity updatedUser = userRepository.save(user);
            LOG.info("User updated with id: {}", updatedUser.getId());

            responseObserver.onNext(mapToProtoUser(updatedUser));
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID format: " + request.getId())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            LOG.error("Failed to update user with id: {}", request.getId(), e);
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