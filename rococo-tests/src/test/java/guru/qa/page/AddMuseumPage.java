package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;
import java.net.URL;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

public class AddMuseumPage extends BasePage<AddMuseumPage> {

    private final SelenideElement modalTitle = $x("//div[contains(@class, 'card')]//header[contains(@class, 'text-2xl')]");

    private final SelenideElement titleInput = $x("//input[@name='title']");
    private final SelenideElement countrySelect = $x("//select[@name='countryId']");
    private final SelenideElement cityInput = $x("//input[@name='city']");
    private final SelenideElement photoInput = $x("//input[@name='photo']");
    private final SelenideElement descriptionTextarea = $x("//textarea[@name='description']");

    private final SelenideElement closeButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-ringed') and contains(text(), 'Закрыть')]");
    private final SelenideElement submitButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-filled-primary') and contains(text(), 'Добавить')]");

    private final SelenideElement titleError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Название музея']]");
    private final SelenideElement countryError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Укажите страну']]");
    private final SelenideElement cityError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Укажите город']]");
    private final SelenideElement photoError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Изображение музея']]");
    private final SelenideElement descriptionError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='О музее']]");

    @Step("Check modal title: {expectedTitle}")
    public AddMuseumPage checkModalTitle(String expectedTitle) {
        modalTitle.shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Set museum title: {title}")
    public AddMuseumPage setTitle(String title) {
        titleInput.setValue(title);
        return this;
    }

    @Step("Select country: {countryName}")
    public AddMuseumPage selectCountry(String countryName) {
        countrySelect.selectOptionContainingText(countryName);
        return this;
    }

    @Step("Set city: {city}")
    public AddMuseumPage setCity(String city) {
        cityInput.setValue(city);
        return this;
    }

    @Step("Upload museum image: {imagePath}")
    public AddMuseumPage setPhoto(String imagePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("images/" + imagePath);
        if (resource == null) {
            throw new IllegalArgumentException("Image not found: images/" + imagePath);
        }
        File file = new File(resource.getFile());
        photoInput.uploadFile(file);
        return this;
    }

    @Step("Set museum description: {description}")
    public AddMuseumPage setDescription(String description) {
        descriptionTextarea.setValue(description);
        return this;
    }

    @Step("Fill museum form")
    public AddMuseumPage fillMuseumForm(String title, String countryName, String city, String imagePath, String description) {
        setTitle(title);
        selectCountry(countryName);
        setCity(city);
        setPhoto(imagePath);
        setDescription(description);
        return this;
    }

    @Step("Click submit button (stay on same page)")
    public AddMuseumPage submit() {
        submitButton.click();
        return this;
    }

    @Step("Submit and navigate to museums page on success")
    public MuseumsPage submitAndGoToMuseums() {
        submitButton.click();
        return new MuseumsPage();
    }

    @Step("Click close button")
    public MuseumsPage close() {
        closeButton.click();
        return new MuseumsPage();
    }

    @Step("Check title field error: {expectedError}")
    public AddMuseumPage checkTitleError(String expectedError) {
        titleError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check country field error: {expectedError}")
    public AddMuseumPage checkCountryError(String expectedError) {
        countryError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check city field error: {expectedError}")
    public AddMuseumPage checkCityError(String expectedError) {
        cityError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check photo field error: {expectedError}")
    public AddMuseumPage checkPhotoError(String expectedError) {
        photoError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check description field error: {expectedError}")
    public AddMuseumPage checkDescriptionError(String expectedError) {
        descriptionError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check validation message on title field when it's empty")
    public AddMuseumPage checkTitleValidationMessage(String expectedMessage) {
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", titleInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on country field when it's empty")
    public AddMuseumPage checkCountryValidationMessage(String expectedMessage) {
        titleInput.setValue("Test Museum");
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", countrySelect);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on city field when it's empty")
    public AddMuseumPage checkCityValidationMessage(String expectedMessage) {
        titleInput.setValue("Test Museum");
        countrySelect.selectOption(0);
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", cityInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on photo field when it's empty")
    public AddMuseumPage checkPhotoValidationMessage(String expectedMessage) {
        titleInput.setValue("Test Museum");
        countrySelect.selectOption(1);
        cityInput.setValue("Test City");
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", photoInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on description field when it's empty")
    public AddMuseumPage checkDescriptionValidationMessage(String expectedMessage) {
        titleInput.setValue("Test Museum");
        countrySelect.selectOption(0);
        cityInput.setValue("Test City");

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("images/test-painting.jpg");
        if (resource != null) {
            File file = new File(resource.getFile());
            photoInput.uploadFile(file);
        }
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", descriptionTextarea);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }
}