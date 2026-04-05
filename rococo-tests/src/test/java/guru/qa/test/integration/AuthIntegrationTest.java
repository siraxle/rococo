package guru.qa.test.integration;

import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.service.AuthClient;
import guru.qa.service.api.AuthApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
@DisplayName("Auth Integration Tests")
@Disabled("не работает получение XSRF-TOKEN")

public class AuthIntegrationTest {

    private final AuthClient authClient = new AuthApiClient();

    @Test
    @DisplayName("Should register new user via API")
    void shouldRegisterUser() throws Exception {
        String username = RandomDataUtils.randomUsername();
        String password = "test123";

        authClient.registerUser(username, password);

        String token = authClient.login(username, password);
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should login and get token for existing user")
    void shouldLoginAndGetToken() throws Exception {
        String username = RandomDataUtils.randomUsername();
        String password = "test123";

        authClient.registerUser(username, password);

        String token = authClient.login(username, password);
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should not login with wrong password")
    void shouldNotLoginWithWrongPassword() throws Exception {
        String username = RandomDataUtils.randomUsername();
        String password = "test123";
        String wrongPassword = "wrongPassword";

        authClient.registerUser(username, password);

        assertThrows(Exception.class, () -> authClient.login(username, wrongPassword));
    }

    @Test
    @DisplayName("Should not register duplicate user")
    void shouldNotRegisterDuplicateUser() throws Exception {
        String username = RandomDataUtils.randomUsername();
        String password = "test123";

        authClient.registerUser(username, password);

        assertThrows(Exception.class, () -> authClient.registerUser(username, password));
    }

    @Test
    @DisplayName("Should register and login in one call")
    void shouldRegisterAndLogin() throws Exception {
        String username = RandomDataUtils.randomUsername();
        String password = "test123";

        String token = authClient.registerAndLogin(username, password);
        assertThat(token).isNotNull().isNotEmpty();
    }
}