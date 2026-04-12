package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

    private final SelenideElement welcomeText = $("p.text-3xl");

    @Step("Check welcome text: {expectedText}")
    public MainPage checkWelcomeText(String expectedText) {
        welcomeText.shouldHave(text(expectedText));
        return this;
    }
}