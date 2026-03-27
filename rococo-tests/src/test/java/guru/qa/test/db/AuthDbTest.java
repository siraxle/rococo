package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.service.AuthClient;
import guru.qa.service.db.AuthDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DbTest
@DisplayName("Auth Database Tests")
public class AuthDbTest {

    private final AuthClient authClient = new AuthDbClient();
    private String testUsername;
    private String testPassword;

    @BeforeEach
    @DisplayName("Setup test user data")
    void setUp() {
        testUsername = RandomDataUtils.randomUsername();
        testPassword = "test123";
    }

    @AfterEach
    @DisplayName("Cleanup test user data")
    void tearDown() {
        if (testUsername != null && authClient.userExists(testUsername)) {
            authClient.deleteUser(testUsername);
        }
    }

    @Test
    @DisplayName("Should create user in auth database")
    void shouldCreateUserInAuthDatabase() {
        authClient.createUser(testUsername, testPassword);

        boolean exists = authClient.userExists(testUsername);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should delete user from auth database")
    void shouldDeleteUserFromAuthDatabase() {
        authClient.createUser(testUsername, testPassword);

        boolean existsBeforeDelete = authClient.userExists(testUsername);
        assertThat(existsBeforeDelete).isTrue();

        authClient.deleteUser(testUsername);

        boolean existsAfterDelete = authClient.userExists(testUsername);
        assertThat(existsAfterDelete).isFalse();
    }

    @Test
    @DisplayName("Should create user with correct password encoding")
    void shouldCreateUserWithCorrectPasswordEncoding() {
        authClient.createUser(testUsername, testPassword);

        String storedPassword = authClient.getUserPassword(testUsername);
        assertThat(storedPassword)
                .isNotEqualTo(testPassword)
                .startsWith("{bcrypt}")
                .hasSizeGreaterThan(10);
    }

    @Test
    @DisplayName("Should create user with read and write authorities")
    void shouldCreateUserWithAuthorities() {
        authClient.createUser(testUsername, testPassword);

        int authoritiesCount = authClient.getUserAuthoritiesCount(testUsername);
        assertThat(authoritiesCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Should not find non-existent user")
    void shouldNotFindNonExistentUser() {
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        boolean exists = authClient.userExists(nonExistentUsername);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return null for non-existent user password")
    void shouldGetNullForNonExistentUserPassword() {
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        String password = authClient.getUserPassword(nonExistentUsername);
        assertThat(password).isNull();
    }

    @Test
    @DisplayName("Should return zero authorities for non-existent user")
    void shouldGetZeroAuthoritiesForNonExistentUser() {
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        int count = authClient.getUserAuthoritiesCount(nonExistentUsername);
        assertThat(count).isEqualTo(0);
    }

}