package guru.qa.jupiter.extension;

import guru.qa.jupiter.annotation.User;
import guru.qa.model.TestData;
import guru.qa.model.UserJson;
import guru.qa.service.db.AuthDbClient;
import guru.qa.service.grpc.UserdataGrpcClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import rococo.grpc.userdata.UserResponse;

import java.util.Optional;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String DEFAULT_PASSWORD = "123456";

    private final AuthDbClient authDbClient = new AuthDbClient();
    private final UserdataGrpcClient userdataGrpcClient = new UserdataGrpcClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    String username = userAnno.username().isEmpty()
                            ? RandomDataUtils.randomUsername()
                            : userAnno.username();
                    String password = DEFAULT_PASSWORD;

                    try {
                        // Create user directly in auth DB
                        authDbClient.createUser(username, password);

                        // Create user profile in userdata-service via gRPC and get the response
                        UserResponse grpcResponse = userdataGrpcClient.createUser(username);

                        // Get user ID from gRPC response
                        String userId = grpcResponse.getId();

                        // Generate random user data
                        String firstname = RandomDataUtils.randomFirstName();
                        String lastname = RandomDataUtils.randomLastName();
                        String avatar = "avatar_" + System.currentTimeMillis() + ".jpg";

                        // Update user with generated data via gRPC
                        userdataGrpcClient.updateUser(userId, firstname, lastname, avatar);

                        // Small delay for synchronization
                        Thread.sleep(500);

                        // Create UserJson with generated data
                        UserJson user = new UserJson(
                                userId,
                                username,
                                firstname,
                                lastname,
                                avatar,
                                null,  // createdAt
                                new TestData(password)
                        );
                        setUser(user);

                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create user: " + username, e);
                    }
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getUser().ifPresent(user -> {
            try {
                // Delete user from userdata service
                userdataGrpcClient.deleteUser(user.id());
                // Delete user from auth DB
                authDbClient.deleteUser(user.username());
            } catch (Exception e) {
                // Log but don't fail test if cleanup fails
                System.err.println("Failed to delete user: " + user.username() + ", error: " + e.getMessage());
            }
        });
        // Clear from context
        context.getStore(NAMESPACE).remove(context.getUniqueId());
    }

    public static void setUser(UserJson user) {
        context().getStore(NAMESPACE).put(context().getUniqueId(), user);
    }

    public static Optional<UserJson> getUser() {
        return Optional.ofNullable(context().getStore(NAMESPACE)
                .get(context().getUniqueId(), UserJson.class));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getUser().orElseThrow(() -> new IllegalStateException("User not found in context"));
    }
}