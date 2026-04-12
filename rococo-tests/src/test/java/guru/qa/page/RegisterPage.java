package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RegisterPage extends BasePage<RegisterPage> {

    private final LoginPage loginPage = new LoginPage();

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement loginLink = $x("//button[contains(text(), 'Войти')]");
    private final SelenideElement loginToSystemLink = $x("//a[contains(text(), 'Войти в систему')]");
    private final SelenideElement userNameError = $x("//span[contains(text(), 'Username')]");
    private final SelenideElement passwordError = $x("//span[contains(text(), 'Passwords should be equal')]");

    @Step("Register new user with username: {username}")
    public RegisterPage register(String username, String password, String confirmPassword) {
        LoginPage loginPage = goToLogin();
        loginPage.goToRegister();
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(confirmPassword);
        submitButton.click();
        return this;
    }

    @Step("Check username error message: {expectedError}")
    public RegisterPage checkUsernameError(String expectedError) {
        userNameError.shouldHave(text(expectedError));
        return this;
    }


    @Step("Check password confirmation error message: {expectedError}")
    public RegisterPage checkPasswordSubmitError(String expectedError) {
        passwordError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Navigate to login page")
    public LoginPage goToLogin() {
        loginLink.click();
        return new LoginPage();
    }

    @Step("Navigate to system login page")
    public LoginPage goToSystemLoginLink() {
        loginToSystemLink.click();
        return new LoginPage();
    }

    @Step("Verify registration page is loaded")
    public void checkPageTitle() {
        submitButton.shouldBe(visible);
    }

    @Step("Check validation message on username field when it's empty")
    public RegisterPage checkUsernameValidationMessage(String expectedMessage) {
        submitButton.click();

        String message = executeJavaScript("return arguments[0].validationMessage;", usernameInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on password field when it's empty")
    public RegisterPage checkPasswordValidationMessage(String expectedMessage) {
        usernameInput.setValue("testuser123");
        passwordSubmitInput.setValue("Test123456");

        submitButton.click();

        String message = executeJavaScript("return arguments[0].validationMessage;", passwordInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on password confirm field when it's empty")
    public RegisterPage checkPasswordConfirmValidationMessage(String expectedMessage) {
        usernameInput.setValue("testuser123");
        passwordInput.setValue("Test123456");

        submitButton.click();

        String message = executeJavaScript("return arguments[0].validationMessage;", passwordSubmitInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }
}