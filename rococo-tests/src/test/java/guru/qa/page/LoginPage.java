package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage extends BasePage<LoginPage> {

    private static final Config CFG = Config.getInstance();

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerLink = $("a[href='/register']");
    private final SelenideElement errorMessage = $(".form__error");
    private final SelenideElement loginButton = $x("//button[contains(text(), 'Войти')]");;
    public MainPage login(String username, String password) {
        loginButton.click();
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    public LoginPage invalidLogin(String username, String password) {
        loginButton.click();
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return this;
    }

    public LoginPage checkError(String expectedError) {
        errorMessage.shouldHave(text(expectedError));
        return this;
    }

    public RegisterPage goToRegister() {
//        loginButton.click();
        registerLink.click();
        return new RegisterPage();
    }
}