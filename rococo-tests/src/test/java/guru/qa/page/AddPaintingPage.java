package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AddPaintingPage extends BasePage<AddPaintingPage> {

    private final SelenideElement titleInput = $("input[name='title']");
    private final SelenideElement imageInput = $("input[name='content']");
    private final SelenideElement authorSelect = $("select[name='authorId']");
    private final SelenideElement descriptionTextarea = $("textarea[name='description']");
    private final SelenideElement museumSelect = $("select[name='museumId']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement closeButton = $$("button.btn").filter(text("Закрыть")).first();
    private final SelenideElement errorMessage = $(".text-error-400");

    public AddPaintingPage setTitle(String title) {
        titleInput.setValue(title);
        return this;
    }

    public AddPaintingPage setImage(String imagePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("images/" + imagePath).getFile());
        imageInput.uploadFile(file);
        return this;
    }

    public AddPaintingPage selectAuthor(String authorName) {
        authorSelect.click();
        SelenideElement option = authorSelect.$$("option").findBy(text(authorName));
        option.click();
        return this;
    }

    public AddPaintingPage setDescription(String description) {
        descriptionTextarea.setValue(description);
        return this;
    }

    public AddPaintingPage selectMuseum(String museumName) {
        museumSelect.click();
        SelenideElement option = museumSelect.$$("option").findBy(text(museumName));
        option.click();
        return this;
    }

    public AddPaintingPage submit() {
        submitButton.click();
        return this;
    }

    public AddPaintingPage close() {
        closeButton.click();
        return this;
    }

    public AddPaintingPage checkError(String expectedError) {
        errorMessage.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check validation message on picture name field when it's empty")
    public AddPaintingPage checkPictureNameValidationMessage(String expectedMessage) {
        submitButton.click();

        String message = executeJavaScript("return arguments[0].validationMessage;", titleInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

    @Step("Check validation message on image Input field when it's empty")
    public AddPaintingPage checkImageInputValidationMessage(String expectedMessage) {
        submitButton.click();

        String message = executeJavaScript("return arguments[0].validationMessage;", imageInput);
        assertThat(message).isEqualTo(expectedMessage);
        return this;
    }

}