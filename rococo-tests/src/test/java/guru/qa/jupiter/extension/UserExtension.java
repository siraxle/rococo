package guru.qa.jupiter.extension;

import guru.qa.config.ApplicationContextHolder;
import guru.qa.jupiter.annotation.User;
import guru.qa.model.TestData;
import guru.qa.model.UserJson;
import guru.qa.service.db.AuthDbClient;
import guru.qa.service.grpc.UserdataGrpcClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import rococo.grpc.userdata.UserResponse;

import java.util.Optional;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class UserExtension implements BeforeEachCallback, AfterEachCallback, AfterAllCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String DEFAULT_PASSWORD = "123456";

    private final AuthDbClient authDbClient;
    private UserdataGrpcClient userdataGrpcClient;

    public UserExtension() {
        this.authDbClient = ApplicationContextHolder.getContext().getBean(AuthDbClient.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        userdataGrpcClient = new UserdataGrpcClient();

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    String username = userAnno.username().isEmpty()
                            ? RandomDataUtils.randomUsername()
                            : userAnno.username();
                    String password = DEFAULT_PASSWORD;

                    try {
                        authDbClient.createUser(username, password);

                        UserResponse grpcResponse = userdataGrpcClient.createUser(username);
                        String userId = grpcResponse.getId();

                        String firstname = RandomDataUtils.randomFirstName();
                        String lastname = RandomDataUtils.randomLastName();
                        String avatar = "avatar_" + System.currentTimeMillis() + ".jpg";

                        userdataGrpcClient.updateUser(userId, firstname, lastname, avatar);

                        Thread.sleep(500);

                        UserJson user = new UserJson(
                                userId,
                                username,
                                firstname,
                                lastname,
                                avatar,
                                null,
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
                if (userdataGrpcClient != null) {
                    userdataGrpcClient.deleteUser(user.id());
                }
                authDbClient.deleteUser(user.username());
            } catch (Exception e) {
                System.err.println("Failed to delete user: " + user.username() + ", error: " + e.getMessage());
            } finally {
                if (userdataGrpcClient != null) {
                    userdataGrpcClient.close();
                }
            }
        });
        context.getStore(NAMESPACE).remove(context.getUniqueId());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        ApplicationContextHolder.closeContext();
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