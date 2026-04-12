package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;
import java.net.URL;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

public class AddArtistPage extends BasePage<AddArtistPage> {

    private final SelenideElement modalTitle = $x("//div[contains(@class, 'card')]//header[contains(@class, 'text-2xl') and contains(text(), 'Новый художник')]");

    private final SelenideElement nameInput = $x("//input[@name='name']");
    private final SelenideElement photoInput = $x("//input[@name='photo']");
    private final SelenideElement biographyTextarea = $x("//textarea[@name='biography']");

    private final SelenideElement closeButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-ringed') and contains(text(), 'Закрыть')]");
    private final SelenideElement submitButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-filled-primary') and contains(text(), 'Добавить')]");

    private final SelenideElement nameError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Имя']]");
    private final SelenideElement photoError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Изображение художника']]");
    private final SelenideElement biographyError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Биография']]");

    @Step("Check modal title")
    public AddArtistPage checkModalTitle() {
        modalTitle.shouldBe(visible);
        return this;
    }

    @Step("Set artist name: {name}")
    public AddArtistPage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Step("Upload artist image: {imagePath}")
    public AddArtistPage setPhoto(String imagePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("images/" + imagePath);
        if (resource == null) {
            throw new IllegalArgumentException("Image not found: images/" + imagePath);
        }
        File file = new File(resource.getFile());
        photoInput.uploadFile(file);
        return this;
    }

    @Step("Set artist biography: {biography}")
    public AddArtistPage setBiography(String biography) {
        biographyTextarea.setValue(biography);
        return this;
    }

    @Step("Fill artist form")
    public AddArtistPage fillArtistForm(String name, String imagePath, String biography) {
        setName(name);
        setPhoto(imagePath);
        setBiography(biography);
        return this;
    }

    @Step("Click submit button (stay on same page)")
    public AddArtistPage submit() {
        submitButton.click();
        return this;
    }

    @Step("Click close button")
    public ArtistsPage close() {
        closeButton.click();
        return new ArtistsPage();
    }

    @Step("Check name field error: {expectedError}")
    public AddArtistPage checkNameError(String expectedError) {
        nameError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check photo field error: {expectedError}")
    public AddArtistPage checkPhotoError(String expectedError) {
        photoError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check biography field error: {expectedError}")
    public AddArtistPage checkBiographyError(String expectedError) {
        biographyError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Submit and navigate to artists page on success")
    public ArtistsPage submitAndGoToArtists() {
        submitButton.click();
        return new ArtistsPage();
    }

    @Step("Check validation message on name field when it's empty")
    public AddArtistPage checkNameValidationMessage(String expectedMessage) {
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", nameInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on biography field when it's empty")
    public AddArtistPage checkBiographyValidationMessage(String expectedMessage) {
        nameInput.setValue("Test Artist");
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", biographyTextarea);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on photo field when it's empty")
    public AddArtistPage checkPhotoValidationMessage(String expectedMessage) {
        nameInput.setValue("Test Artist");
        biographyTextarea.setValue("Test Biography");
        submit();

        String message = executeJavaScript("return arguments[0].validationMessage;", photoInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }
}