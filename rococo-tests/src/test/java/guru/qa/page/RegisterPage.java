package guru.qa.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class RegisterPage extends BasePage<RegisterPage> {

    private final LoginPage loginPage = new LoginPage();

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement loginLink = $x("//button[contains(text(), 'Войти')]");
    private final SelenideElement loginToSystemLink = $x("//a[contains(text(), 'Войти в систему')]");
    private final SelenideElement userNameError = $x("//span[contains(text(), 'Username ')]");

    public RegisterPage register(String username, String password, String confirmPassword) {
        loginLink.click();
        loginPage.goToRegister();
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(confirmPassword);
        submitButton.click();
        return this;
    }

    public RegisterPage checkUsernameError(String expectedError) {
        userNameError.shouldHave(text(expectedError));
        return this;
    }

    public RegisterPage checkPasswordError(String expectedError) {
        $(".error__password").shouldHave(text(expectedError));
        return this;
    }

    public RegisterPage checkPasswordSubmitError(String expectedError) {
        $(".error__passwordSubmit").shouldHave(text(expectedError));
        return this;
    }

    public LoginPage goToLogin() {
        loginLink.click();
        return new LoginPage();
    }

    public LoginPage goToSystemLoginLink() {
        loginToSystemLink.click();
        return new LoginPage();
    }

    public void checkPageTitle() {
        submitButton.shouldBe(visible);
    }
}