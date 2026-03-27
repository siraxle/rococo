package guru.qa.test.integration;

import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.UserJson;
import guru.qa.service.UserClient;
import guru.qa.service.api.UserApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
public class UserIntegrationTest {

    private final UserClient userClient = new UserApiClient();

    @User
    @Test
    void shouldCreateUserViaExtension(UserJson user) {
        assertThat(user.id()).isNotNull();
        assertThat(user.username()).isNotNull();
        assertThat(user.firstname()).isNotNull();
        assertThat(user.lastname()).isNotNull();
    }

    @User
    @Test
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
    void shouldGetUserByUsername(UserJson user) {
        UserJson fetchedUser = userClient.getUserByUsername(user.username());

        assertThat(fetchedUser.id()).isEqualTo(user.id());
        assertThat(fetchedUser.username()).isEqualTo(user.username());
        assertThat(fetchedUser.firstname()).isEqualTo(user.firstname());
        assertThat(fetchedUser.lastname()).isEqualTo(user.lastname());
    }

    @User
    @Test
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
    void shouldReturn404ForNonExistentUser() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.getUserById(nonExistentId);
        });

        assertThat(exception.getMessage()).contains("Failed to get user");
    }

    @User
    @Test
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
    void shouldVerifyUserExists(UserJson user) {
        boolean exists = userClient.existsById(user.id());
        assertThat(exists).isTrue();

        boolean existsByUsername = userClient.existsByUsername(user.username());
        assertThat(existsByUsername).isTrue();
    }

    @Test
    void shouldVerifyUserDoesNotExist() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";
        String nonExistentUsername = "nonexistent_user_" + System.currentTimeMillis();

        boolean exists = userClient.existsById(nonExistentId);
        assertThat(exists).isFalse();

        boolean existsByUsername = userClient.existsByUsername(nonExistentUsername);
        assertThat(existsByUsername).isFalse();
    }
}