package guru.qa.test.integration;

import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.UserJson;
import guru.qa.service.UserClient;
import guru.qa.service.api.UserApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
@DisplayName("User Integration Tests")
public class UserIntegrationTest {

    private final UserClient userClient = new UserApiClient();

    @User
    @Test
    @DisplayName("Should create user via extension")
    void shouldCreateUserViaExtension(UserJson user) {
        assertThat(user.id()).isNotNull();
        assertThat(user.username()).isNotNull();
        assertThat(user.firstname()).isNotNull();
        assertThat(user.lastname()).isNotNull();
    }

    @User
    @Test
    @DisplayName("Should get user by id via API")
    void shouldGetUserById(UserJson user) {
        UserJson fetchedUser = userClient.getUserById(user.id());

        assertThat(fetchedUser.id()).isEqualTo(user.id());
        assertThat(fetchedUser.username()).isEqualTo(user.username());
        assertThat(fetchedUser.firstname()).isEqualTo(user.firstname());
        assertThat(fetchedUser.lastname()).isEqualTo(user.lastname());
        assertThat(fetchedUser.avatar()).isEqualTo(user.avatar());
    }

    @User
    @Test
    @DisplayName("Should get user by username via API")
    void shouldGetUserByUsername(UserJson user) {
        UserJson fetchedUser = userClient.getUserByUsername(user.username());

        assertThat(fetchedUser.id()).isEqualTo(user.id());
        assertThat(fetchedUser.username()).isEqualTo(user.username());
        assertThat(fetchedUser.firstname()).isEqualTo(user.firstname());
        assertThat(fetchedUser.lastname()).isEqualTo(user.lastname());
        assertThat(fetchedUser.avatar()).isEqualTo(user.avatar());
    }

    @User
    @Test
    @DisplayName("Should update user via API")
    void shouldUpdateUser(UserJson user) {
        String newFirstname = RandomDataUtils.randomFirstName();
        String newLastname = RandomDataUtils.randomLastName();
        String newAvatar = "avatar_" + System.currentTimeMillis() + ".jpg";

        UserJson updatedUser = userClient.updateUser(user.id(), newFirstname, newLastname, newAvatar);

        assertThat(updatedUser.id()).isEqualTo(user.id());
        assertThat(updatedUser.firstname()).isEqualTo(newFirstname);
        assertThat(updatedUser.lastname()).isEqualTo(newLastname);
        assertThat(updatedUser.avatar()).isEqualTo(newAvatar);
        assertThat(updatedUser.username()).isEqualTo(user.username());
    }

    @Test
    @DisplayName("Should return 404 for non-existent user")
    void shouldReturn404ForNonExistentUser() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.getUserById(nonExistentId);
        });

        assertThat(exception.getMessage()).contains("Failed to get user");
    }

    @User
    @Test
    @DisplayName("Should not create duplicate user")
    void shouldNotCreateDuplicateUser(UserJson user) {
        UserJson duplicateUser = new UserJson(
                null,
                user.username(),
                RandomDataUtils.randomFirstName(),
                RandomDataUtils.randomLastName(),
                "avatar.jpg",
                null,
                null
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.createUser(duplicateUser);
        });

        assertThat(exception.getMessage()).contains("Failed to create user");
    }

    @User
    @Test
    @DisplayName("Should verify user exists by id and username")
    void shouldVerifyUserExists(UserJson user) {
        boolean exists = userClient.existsById(user.id());
        assertThat(exists).isTrue();

        boolean existsByUsername = userClient.existsByUsername(user.username());
        assertThat(existsByUsername).isTrue();
    }

    @Test
    @DisplayName("Should verify non-existent user does not exist")
    void shouldVerifyUserDoesNotExist() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        boolean exists = userClient.existsById(nonExistentId);
        assertThat(exists).isFalse();

        boolean existsByUsername = userClient.existsByUsername(nonExistentUsername);
        assertThat(existsByUsername).isFalse();
    }
}