package guru.qa.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.api.core.CodeInterceptor;
import guru.qa.api.core.ThreadSafeCookieStore;
import guru.qa.config.Config;
import guru.qa.jupiter.extension.ApiLoginExtension;
import guru.qa.service.RestClient;
import guru.qa.utils.OauthUtils;
import io.qameta.allure.Step;
import okhttp3.ResponseBody;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "client";
    private static final String SCOPE = "openid";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String REDIRECT_URI = CFG.frontUrl() + "authorized";

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @Step("Register user: {username}")
    @Nonnull
    public void registerUser(String username, String password) throws IOException, InterruptedException {
        // Get registration form (to obtain XSRF token)
        Response<ResponseBody> formResponse = authApi.requestRegisterForm().execute();

        if (!formResponse.isSuccessful()) {
            throw new IOException("Failed to get registration form. Code: " + formResponse.code());
        }

        // Small delay to ensure cookies are set
        TimeUnit.MILLISECONDS.sleep(100);

        String xsrfToken = ThreadSafeCookieStore.INSTANCE.xsrfCookie();
        if (xsrfToken == null) {
            throw new IOException("XSRF-TOKEN not found in cookies");
        }

        // Register
        Response<Void> registerResponse = authApi.register(
                username,
                password,
                password,
                xsrfToken
        ).execute();

        if (!registerResponse.isSuccessful()) {
            throw new IOException("Registration failed with code: " + registerResponse.code());
        }
    }

    @Step("Login and get token for user: {username}")
    @Nonnull
    public String login(String username, String password) throws IOException {
        String codeVerifier = OauthUtils.generateCodeVerifier();
        String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);

        // Authorize
        authApi.authorize(
                RESPONSE_TYPE,
                CLIENT_ID,
                SCOPE,
                REDIRECT_URI,
                codeChallenge,
                CODE_CHALLENGE_METHOD
        ).execute();

        // Small delay
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Login
        String xsrfToken = ThreadSafeCookieStore.INSTANCE.xsrfCookie();
        Response<Void> loginResponse = authApi.login(
                username,
                password,
                xsrfToken
        ).execute();

        if (!loginResponse.isSuccessful()) {
            throw new IOException("Login failed with code: " + loginResponse.code());
        }

        String code = ApiLoginExtension.getCode();
        if (code == null) {
            throw new IOException("Authorization code not found");
        }

        // Get token
        Response<JsonNode> tokenResponse = authApi.token(
                code,
                REDIRECT_URI,
                CLIENT_ID,
                codeVerifier,
                GRANT_TYPE
        ).execute();

        if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
            JsonNode tokenNode = tokenResponse.body();
            if (tokenNode.has("id_token")) {
                return tokenNode.get("id_token").asText();
            } else {
                throw new IOException("id_token not found in token response");
            }
        } else {
            throw new IOException("Token request failed with code: " + tokenResponse.code());
        }
    }

    @Step("Register and login user: {username}")
    @Nonnull
    public String registerAndLogin(String username, String password) throws IOException, InterruptedException {
        registerUser(username, password);
        waitForUserRegistration(username);
        return login(username, password);
    }

    private void waitForUserRegistration(String username) throws InterruptedException {
        // Give some time for user to be created in auth database
        TimeUnit.SECONDS.sleep(1);
    }
}