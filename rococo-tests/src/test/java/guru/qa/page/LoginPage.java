package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage extends BasePage<LoginPage> {

//    private static final Config CFG = Config.getInstance();

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerLink = $("a[href='/register']");
    private final SelenideElement errorMessage = $(".form__error");
    private final SelenideElement loginButton = $x("//button[contains(text(), 'Войти')]");

    @Step("Login with username: {username}")
    public MainPage login(String username, String password) {
        loginButton.click();
        setUsername(username);
        setPassword(password);
        clickSubmit();
        return new MainPage();
    }

    @Step("Attempt to login with invalid credentials: {username}")
    public LoginPage invalidLogin(String username, String password) {
        loginButton.click();
        setUsername(username);
        setPassword(password);
        clickSubmit();
        return this;
    }

    @Step("Set username: {username}")
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password")
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Click submit button")
    public LoginPage clickSubmit() {
        submitButton.click();
        return this;
    }

    @Step("Get username field")
    public SelenideElement getUsernameField() {
        return usernameInput;
    }

    @Step("Get password field")
    public SelenideElement getPasswordField() {
        return passwordInput;
    }

    @Step("Get submit button")
    public SelenideElement getSubmitButton() {
        return submitButton;
    }

    @Step("Check error message: {expectedError}")
    public LoginPage checkError(String expectedError) {
        errorMessage.shouldHave(text(expectedError));
        return this;
    }

    @Step("Navigate to registration page")
    public RegisterPage goToRegister() {
        registerLink.click();
        return new RegisterPage();
    }

    @Step("Navigate to registration page from main page")
    public RegisterPage goToRegisterFromMainPage() {
        loginButton.click();
        registerLink.click();
        return new RegisterPage();
    }
}