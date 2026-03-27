package guru.qa.test.db;

import guru.qa.jupiter.annotation.meta.DbTest;
import guru.qa.model.UserJson;
import guru.qa.service.UserClient;
import guru.qa.service.db.UserDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
@DisplayName("User Database Tests")
public class UserDbTest {

    private final UserClient userClient = new UserDbClient();
    private UserJson testUser;

    @BeforeEach
    @DisplayName("Setup test user data")
    void setUp() {
        String id = RandomDataUtils.randomId();
        String username = RandomDataUtils.randomUsername();
        String firstname = RandomDataUtils.randomFirstName();
        String lastname = RandomDataUtils.randomLastName();
        String avatar = "avatar_" + System.currentTimeMillis() + ".jpg";

        testUser = new UserJson(id, username, firstname, lastname, avatar, null, null);
        userClient.createUser(testUser);
    }

    @AfterEach
    @DisplayName("Cleanup test user data")
    void tearDown() {
        if (testUser != null && testUser.id() != null && userClient.existsById(testUser.id())) {
            userClient.deleteUser(testUser.id());
        }
    }

    @Test
    @DisplayName("Should create and save user to database")
    void shouldCreateAndSaveUserToDatabase() {
        boolean exists = userClient.existsById(testUser.id());
        assertThat(exists).isTrue();

        UserJson dbUser = userClient.getUserById(testUser.id());
        assertThat(dbUser.id()).isEqualTo(testUser.id());
        assertThat(dbUser.username()).isEqualTo(testUser.username());
        assertThat(dbUser.firstname()).isEqualTo(testUser.firstname());
        assertThat(dbUser.lastname()).isEqualTo(testUser.lastname());
        assertThat(dbUser.avatar()).isEqualTo(testUser.avatar());
    }

    @Test
    @DisplayName("Should read user from database by id")
    void shouldReadUserByIdFromDatabase() {
        UserJson dbUser = userClient.getUserById(testUser.id());
        assertThat(dbUser).isNotNull();
        assertThat(dbUser.id()).isEqualTo(testUser.id());
        assertThat(dbUser.username()).isEqualTo(testUser.username());
        assertThat(dbUser.firstname()).isEqualTo(testUser.firstname());
        assertThat(dbUser.lastname()).isEqualTo(testUser.lastname());
        assertThat(dbUser.avatar()).isEqualTo(testUser.avatar());
    }

    @Test
    @DisplayName("Should read user from database by username")
    void shouldReadUserByUsernameFromDatabase() {
        UserJson dbUser = userClient.getUserByUsername(testUser.username());
        assertThat(dbUser).isNotNull();
        assertThat(dbUser.id()).isEqualTo(testUser.id());
        assertThat(dbUser.username()).isEqualTo(testUser.username());
        assertThat(dbUser.firstname()).isEqualTo(testUser.firstname());
        assertThat(dbUser.lastname()).isEqualTo(testUser.lastname());
        assertThat(dbUser.avatar()).isEqualTo(testUser.avatar());
    }

    @Test
    @DisplayName("Should update user in database")
    void shouldUpdateUserInDatabase() {
        String newFirstname = RandomDataUtils.randomFirstName();
        String newLastname = RandomDataUtils.randomLastName();
        String newAvatar = "new_avatar_" + System.currentTimeMillis() + ".jpg";

        UserJson updatedUser = userClient.updateUser(testUser.id(), newFirstname, newLastname, newAvatar);

        assertThat(updatedUser.firstname()).isEqualTo(newFirstname);
        assertThat(updatedUser.lastname()).isEqualTo(newLastname);
        assertThat(updatedUser.avatar()).isEqualTo(newAvatar);

        UserJson dbUser = userClient.getUserById(testUser.id());
        assertThat(dbUser.firstname()).isEqualTo(newFirstname);
        assertThat(dbUser.lastname()).isEqualTo(newLastname);
        assertThat(dbUser.avatar()).isEqualTo(newAvatar);
        assertThat(dbUser.username()).isEqualTo(testUser.username());
    }

    @Test
    @DisplayName("Should delete user from database")
    void shouldDeleteUserFromDatabase() {
        userClient.deleteUser(testUser.id());

        boolean exists = userClient.existsById(testUser.id());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get all users from database and contain test user")
    void shouldGetAllUsersFromDatabase() {
        var users = userClient.getAllUsers();
        assertThat(users).isNotEmpty();

        boolean found = users.stream()
                .anyMatch(u -> u.id().equals(testUser.id()));
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("Should not find non-existent user by id")
    void shouldNotFindNonExistentUserById() {
        String nonExistentId = RandomDataUtils.randomId();
        boolean exists = userClient.existsById(nonExistentId);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should not find non-existent user by username")
    void shouldNotFindNonExistentUserByUsername() {
        String nonExistentUsername = "nonexistent_" + System.currentTimeMillis();
        boolean exists = userClient.existsByUsername(nonExistentUsername);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should throw exception for non-existent user by id")
    void shouldThrowExceptionForNonExistentUserById() {
        String nonExistentId = RandomDataUtils.randomId();

        assertThatThrownBy(() -> userClient.getUserById(nonExistentId))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("Should throw exception for non-existent user by username")
    void shouldThrowExceptionForNonExistentUserByUsername() {
        String nonExistentUsername = "nonexistent_" + System.currentTimeMillis();

        assertThatThrownBy(() -> userClient.getUserByUsername(nonExistentUsername))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("Should update only specified fields")
    void shouldUpdateOnlySpecifiedFields() {
        String newFirstname = RandomDataUtils.randomFirstName();

        UserJson updatedUser = userClient.updateUser(testUser.id(), newFirstname, null, null);

        assertThat(updatedUser.firstname()).isEqualTo(newFirstname);
        assertThat(updatedUser.lastname()).isEqualTo(testUser.lastname());
        assertThat(updatedUser.avatar()).isEqualTo(testUser.avatar());

        UserJson dbUser = userClient.getUserById(testUser.id());
        assertThat(dbUser.firstname()).isEqualTo(newFirstname);
        assertThat(dbUser.lastname()).isEqualTo(testUser.lastname());
        assertThat(dbUser.avatar()).isEqualTo(testUser.avatar());
    }

    @Test
    @DisplayName("Should create user with unique username")
    void shouldCreateUserWithUniqueUsername() {
        UserJson duplicateUser = new UserJson(
                RandomDataUtils.randomId(),
                testUser.username(),
                RandomDataUtils.randomFirstName(),
                RandomDataUtils.randomLastName(),
                "avatar.jpg",
                null,
                null
        );

        assertThatThrownBy(() -> userClient.createUser(duplicateUser))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should verify user exists by username after creation")
    void shouldVerifyUserExistsByUsernameAfterCreation() {
        boolean exists = userClient.existsByUsername(testUser.username());
        assertThat(exists).isTrue();
    }
}