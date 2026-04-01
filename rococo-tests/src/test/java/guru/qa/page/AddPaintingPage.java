package guru.qa.page;

import com.codeborne.selenide.SelenideElement;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

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
        imageInput.uploadFile(new File(imagePath));
        return this;
    }

    public AddPaintingPage selectAuthor(String authorName) {
        authorSelect.selectOptionContainingText(authorName);
        return this;
    }

    public AddPaintingPage setDescription(String description) {
        descriptionTextarea.setValue(description);
        return this;
    }

    public AddPaintingPage selectMuseum(String museumName) {
        museumSelect.selectOptionContainingText(museumName);
        return this;
    }

    public PaintingsPage submit() {
        submitButton.click();
        return new PaintingsPage();
    }

    public AddPaintingPage close() {
        closeButton.click();
        return this;
    }

    public AddPaintingPage checkError(String expectedError) {
        errorMessage.shouldHave(text(expectedError));
        return this;
    }
}