package guru.qa.jupiter.extension;

import guru.qa.jupiter.annotation.ApiLogin;
import guru.qa.jupiter.annotation.Token;
import guru.qa.model.UserJson;
import guru.qa.service.api.AuthApiClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.IOException;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    UserJson user = UserExtension.getUser()
                            .orElseThrow(() -> new IllegalStateException("User not found in context. Use @User annotation."));

                    try {
                        String token = authApiClient.login(user.username(), user.testData().password());
                        setToken(token);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to login user: " + user.username(), e);
                    }
                });
    }

    public static void setToken(String token) {
        context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return context().getStore(NAMESPACE).get("code", String.class);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return "Bearer " + getToken();
    }
}