package guru.qa.jupiter.extension;

import guru.qa.jupiter.annotation.User;
import guru.qa.model.TestData;
import guru.qa.model.UserJson;
import guru.qa.service.db.AuthDbClient;
import guru.qa.service.grpc.UserdataGrpcClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import rococo.grpc.userdata.UserResponse;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

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

                        // Small delay for synchronization
                        Thread.sleep(500);

                        // Create UserJson with ID from gRPC response
                        UserJson user = new UserJson(userId, username, null, null, null, new TestData(password));
                        setUser(user);

                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create user: " + username, e);
                    }
                });
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