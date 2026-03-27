package guru.qa.test.integration;

import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.UserJson;
import guru.qa.service.api.UserApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RestTest
public class UserIntegrationTest {

    private final UserApiClient userApiClient = new UserApiClient();

    @User
    @Test
    void shouldCreateUserViaExtension(UserJson user) {
        assertThat(user.id()).isNotNull();
        assertThat(user.username()).isNotNull();
    }

    @User
    @Test
    void shouldGetUserById(UserJson user) {
        UserJson fetchedUser = userApiClient.getUserById(user.id());

        assertThat(fetchedUser.id()).isEqualTo(user.id());
        assertThat(fetchedUser.username()).isEqualTo(user.username());
        assertThat(fetchedUser.firstname()).isEqualTo(user.firstname());
        assertThat(fetchedUser.lastname()).isEqualTo(user.lastname());
    }

    @User
    @Test
    void shouldGetUserByUsername(UserJson user) {
        UserJson fetchedUser = userApiClient.getUserByUsername(user.username());

        assertThat(fetchedUser.id()).isEqualTo(user.id());
        assertThat(fetchedUser.username()).isEqualTo(user.username());
    }

    @User
    @Test
    void shouldUpdateUser(UserJson user) {
        String newFirstname = RandomDataUtils.randomFirstName();
        String newLastname = RandomDataUtils.randomLastName();
        String newAvatar = "avatar_" + System.currentTimeMillis() + ".jpg";

        UserJson userUpdate = new UserJson(
                user.id(),
                user.username(),
                newFirstname,
                newLastname,
                newAvatar,
                null,
                null
        );
        UserJson updatedUser = userApiClient.updateUser(user.id(), userUpdate);

        assertThat(updatedUser.firstname()).isEqualTo(newFirstname);
        assertThat(updatedUser.lastname()).isEqualTo(newLastname);
        assertThat(updatedUser.avatar()).isEqualTo(newAvatar);
    }


    @Test
    void shouldReturn404ForNonExistentUser() {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";

        assertThrows(RuntimeException.class, () -> {
            userApiClient.getUserById(nonExistentId);
        });
    }

    @User
    @Test
    void shouldNotCreateDuplicateUser(UserJson user) {
        UserJson duplicateUser = new UserJson(
                null,
                user.username(),
                user.firstname(),
                user.lastname(),
                user.avatar(),
                null,
                null
        );

        assertThrows(RuntimeException.class, () -> {
            userApiClient.createUser(duplicateUser);
        });
    }
}