package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.service.db.AuthDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
public class AuthDbTest {

    private final AuthDbClient authDbClient = new AuthDbClient();
    private String testUsername;
    private String testPassword;

    @BeforeEach
    void setUp() {
        testUsername = RandomDataUtils.randomUsername();
        testPassword = "test123";
    }

    @AfterEach
    void tearDown() {
        if (testUsername != null && authDbClient.userExists(testUsername)) {
            authDbClient.deleteUser(testUsername);
        }
    }

    @Test
    void shouldCreateUserInAuthDatabase() {
        authDbClient.createUser(testUsername, testPassword);

        boolean exists = authDbClient.userExists(testUsername);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldDeleteUserFromAuthDatabase() {
        authDbClient.createUser(testUsername, testPassword);

        boolean existsBeforeDelete = authDbClient.userExists(testUsername);
        assertThat(existsBeforeDelete).isTrue();

        authDbClient.deleteUser(testUsername);

        boolean existsAfterDelete = authDbClient.userExists(testUsername);
        assertThat(existsAfterDelete).isFalse();
    }

    @Test
    void shouldCreateUserWithCorrectPasswordEncoding() {
        authDbClient.createUser(testUsername, testPassword);

        String storedPassword = authDbClient.getUserPassword(testUsername);
        assertThat(storedPassword)
                .isNotEqualTo(testPassword)
                .startsWith("{bcrypt}")
                .hasSizeGreaterThan(10);
    }

    @Test
    void shouldCreateUserWithAuthorities() {
        authDbClient.createUser(testUsername, testPassword);

        int authoritiesCount = authDbClient.getUserAuthoritiesCount(testUsername);
        assertThat(authoritiesCount).isEqualTo(2);
    }

    @Test
    void shouldNotFindNonExistentUser() {
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        boolean exists = authDbClient.userExists(nonExistentUsername);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldGetNullForNonExistentUserPassword() {
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        String password = authDbClient.getUserPassword(nonExistentUsername);
        assertThat(password).isNull();
    }

    @Test
    void shouldGetZeroAuthoritiesForNonExistentUser() {
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        int count = authDbClient.getUserAuthoritiesCount(nonExistentUsername);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldNotCreateDuplicateUser() {
        authDbClient.createUser(testUsername, testPassword);

        assertThatThrownBy(() -> authDbClient.createUser(testUsername, "differentPassword"))
                .isInstanceOf(RuntimeException.class);
    }
}