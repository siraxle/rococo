package guru.qa.test.integration;

import guru.qa.jupiter.annotation.ApiLogin;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.UserJson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RestTest
public class UserIntegrationTest {

//    @ApiLogin
    @User
    @Test
    void shouldCreateUserViaExtension(UserJson user) {
        assertThat(user.id()).isNotNull();
        assertThat(user.username()).isNotNull();
    }
}