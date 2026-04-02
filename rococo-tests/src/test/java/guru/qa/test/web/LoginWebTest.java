package guru.qa.test.web;

import guru.qa.config.Config;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.WebTest;
import guru.qa.model.UserJson;
import guru.qa.page.MainPage;
import guru.qa.page.LoginPage;
import guru.qa.page.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@DisplayName("Web Login Tests")
public class LoginWebTest {

    private final Config CFG = Config.getInstance();

    @Test
    @User
    @DisplayName("Should login with valid credentials")
    void userShouldLoginWithValidCredentials(UserJson user) {
        MainPage mainPage = open(CFG.frontUrl() + "login", LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage.checkWelcomeText("Ваши любимые картины и художники всегда рядом");
    }

    @Test
    @DisplayName("Should see error with invalid credentials")
    void userShouldSeeErrorWithInvalidCredentials() {
        LoginPage loginPage = open(CFG.frontUrl() + "login", LoginPage.class)
                .invalidLogin("invaliduser_" + System.currentTimeMillis(), "wrongpass");

        loginPage.checkError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Should navigate to register page")
    void userShouldNavigateToRegisterPage() {
        RegisterPage registerPage = open(CFG.frontUrl() + "login", LoginPage.class)
                .goToRegisterFromMainPage();

        registerPage.checkPageTitle();
    }

    @Test
    @User(username = "customuser", password = "custompass123")
    @DisplayName("Should login with custom user credentials")
    void userShouldLoginWithCustomCredentials(UserJson user) {
        MainPage mainPage = open(CFG.frontUrl() + "login", LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage.checkWelcomeText("Ваши любимые картины и художники всегда рядом");
    }
}