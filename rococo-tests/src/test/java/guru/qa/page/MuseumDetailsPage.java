package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

public class MuseumDetailsPage extends BasePage<MuseumDetailsPage> {

    private final SelenideElement museumTitle = $x("//article[contains(@class, 'card')]//header[contains(@class, 'card-header') and contains(@class, 'font-bold')]");
    private final SelenideElement museumLocation = $x("//article[contains(@class, 'card')]//div[contains(@class, 'text-center')][not(contains(@class, 'card-header'))]");
    private final SelenideElement museumDescription = $x("//article[contains(@class, 'card')]//div[contains(@class, 'm-4') and not(contains(@class, 'w-56'))]");
    private final SelenideElement museumImage = $x("//article[contains(@class, 'card')]//img");
    private final SelenideElement editButton = $x("//div[contains(@class, 'w-56')]//button[contains(@class, 'btn')]");

    @Step("Check museum title: {expectedTitle}")
    public MuseumDetailsPage checkTitle(String expectedTitle) {
        museumTitle.shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Check museum location: {expectedLocation}")
    public MuseumDetailsPage checkLocation(String expectedLocation) {
        museumLocation.shouldHave(text(expectedLocation));
        return this;
    }

    @Step("Check museum description: {expectedDescription}")
    public MuseumDetailsPage checkDescription(String expectedDescription) {
        museumDescription.shouldHave(text(expectedDescription));
        return this;
    }

    @Step("Check that museum image is loaded")
    public MuseumDetailsPage checkImageLoaded() {
        museumImage.shouldBe(visible);
        return this;
    }

    @Step("Get museum title")
    public String getTitle() {
        return museumTitle.getText();
    }

    @Step("Get museum location")
    public String getLocation() {
        return museumLocation.getText();
    }

    @Step("Get museum description")
    public String getDescription() {
        return museumDescription.getText();
    }

    @Step("Check validation message on title field when it's empty")
    public MuseumDetailsPage checkTitleValidationMessage(String expectedMessage) {
        String message = executeJavaScript("return arguments[0].validationMessage;", museumTitle);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on location field when it's empty")
    public MuseumDetailsPage checkLocationValidationMessage(String expectedMessage) {
        String message = executeJavaScript("return arguments[0].validationMessage;", museumLocation);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on description field when it's empty")
    public MuseumDetailsPage checkDescriptionValidationMessage(String expectedMessage) {
        String message = executeJavaScript("return arguments[0].validationMessage;", museumDescription);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on image field when it's empty")
    public MuseumDetailsPage checkImageValidationMessage(String expectedMessage) {
        String message = executeJavaScript("return arguments[0].validationMessage;", museumImage);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }
}