package guru.qa.test.web;

import guru.qa.config.Config;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.WebTest;
import guru.qa.model.UserJson;
import guru.qa.page.MainPage;
import guru.qa.page.RegisterPage;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@DisplayName("Web Registration Tests")
public class RegisterWebTest {

    private final Config CFG = Config.getInstance();

    private static final String DEFAULT_PASSWORD = "123456";

    @Test
    @DisplayName("Should register new user with valid data")
    void userShouldRegisterWithValidData() {
        String username = RandomDataUtils.randomUsername();
        String password = DEFAULT_PASSWORD;

        MainPage mainPage = open(CFG.frontUrl() + "register", RegisterPage.class)
                .register(username, password, password)
                .goToSystemLoginLink()
                .login(username, password);

        mainPage.checkWelcomeText("Ваши любимые картины и художники всегда рядом");
    }

    @User
    @Test
    @DisplayName("Should show error when username already exists")
    void userShouldSeeErrorWhenUsernameExists(UserJson user) {
        String username = user.username();
        String password = DEFAULT_PASSWORD;

        RegisterPage registerPage = open(CFG.frontUrl() + "register", RegisterPage.class);
        registerPage.register(username, password, password);

        registerPage.checkUsernameError("Username `" + username + "` already exists");
    }

    @Test
    @DisplayName("Should show error when passwords do not match")
    void userShouldSeeErrorWhenPasswordsDoNotMatch() {
        String username = RandomDataUtils.randomUsername();

        RegisterPage registerPage = open(CFG.frontUrl() + "/register", RegisterPage.class)
                .register(username, "password123", "password456");

        registerPage.checkPasswordSubmitError("Passwords should be equal");
    }

    @Test
    @DisplayName("Should show validation when username is empty")
    void userShouldSeeValidationWhenUsernameEmpty() {
        RegisterPage registerPage = open(CFG.frontUrl() + "register", RegisterPage.class)
                .register("", "", "");
        registerPage.checkUsernameValidationMessage("Заполните это поле.");
    }

    @Test
    @DisplayName("Should show validation when password is empty")
    void userShouldSeeValidationWhenPasswordEmpty() {
        RegisterPage registerPage = open(CFG.frontUrl() + "register", RegisterPage.class)
                .register("", "", "");

        registerPage.checkPasswordValidationMessage("Заполните это поле.");
    }

    @Test
    @DisplayName("Should show validation when password confirm is empty")
    void userShouldSeeValidationWhenPasswordConfirmEmpty() {
        RegisterPage registerPage = open(CFG.frontUrl() + "register", RegisterPage.class)
                .register("", "", "");

        registerPage.checkPasswordConfirmValidationMessage("Заполните это поле.");
    }

}