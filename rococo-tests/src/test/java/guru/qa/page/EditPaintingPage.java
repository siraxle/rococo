package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class EditPaintingPage extends BasePage<EditPaintingPage> {

    // Заголовок модального окна
    private final SelenideElement modalTitle = $x("//div[contains(@class, 'card')]//header[contains(@class, 'text-2xl') and contains(text(), 'Редактировать картину')]");

    // Изображение
    private final SelenideElement currentImage = $x("//div[contains(@class, 'card')]//img[contains(@class, 'max-w-full')]");
    private final SelenideElement imageInput = $x("//input[@name='content']");

    // Поля формы
    private final SelenideElement titleInput = $x("//input[@name='title']");
    private final SelenideElement authorSelect = $x("//select[@name='authorId']");
    private final SelenideElement descriptionTextarea = $x("//textarea[@name='description']");
    private final SelenideElement museumSelect = $x("//select[@name='museumId']");

    // Кнопки
    private final SelenideElement closeButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-ringed') and contains(text(), 'Закрыть')]");
    private final SelenideElement saveButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-filled-primary') and contains(text(), 'Сохранить')]");

    // Ошибки
    private final SelenideElement titleError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Название картины']]");
    private final SelenideElement authorError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Укажите автора картины']]");
    private final SelenideElement descriptionError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Описание картины']]");
    private final SelenideElement museumError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Укажите, где хранится оригинал']]");
    private final SelenideElement imageError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Обновить изображение картины']]");

    @Step("Check modal title")
    public EditPaintingPage checkModalTitle() {
        modalTitle.shouldBe(visible);
        return this;
    }

    @Step("Check current image is visible")
    public EditPaintingPage checkCurrentImageVisible() {
        currentImage.shouldBe(visible);
        return this;
    }

//    @Step("Проверить, что текущее изображение имеет src: {expectedSrc}")
//    public EditPaintingPage checkCurrentImageSrc(String expectedSrc) {
//        currentImage.shouldHave(org.openqa.selenium.By.tagName("img"),
//                new com.codeborne.selenide.Condition("src") {
////                    @Override
//                    public boolean apply(com.codeborne.selenide.Driver driver, org.openqa.selenium.WebElement element) {
//                        return element.getAttribute("src").contains(expectedSrc);
//                    }
//                });
//        return this;
//    }

    @Step("Upload new image: {imagePath}")
    public EditPaintingPage setImage(String imagePath) {
        imageInput.uploadFile(new File(imagePath));
        return this;
    }

    @Step("Set painting title: {title}")
    public EditPaintingPage setTitle(String title) {
        titleInput.clear();
        titleInput.setValue(title);
        return this;
    }

    @Step("Select author: {authorName}")
    public EditPaintingPage selectAuthor(String authorName) {
        authorSelect.selectOptionContainingText(authorName);
        return this;
    }

    @Step("Set description: {description}")
    public EditPaintingPage setDescription(String description) {
        descriptionTextarea.clear();
        descriptionTextarea.setValue(description);
        return this;
    }

    @Step("Select museum: {museumName}")
    public EditPaintingPage selectMuseum(String museumName) {
        museumSelect.selectOptionContainingText(museumName);
        return this;
    }

    @Step("Fill edit painting form")
    public EditPaintingPage fillEditForm(String title, String authorName, String description, String museumName) {
        setTitle(title);
        selectAuthor(authorName);
        setDescription(description);
        selectMuseum(museumName);
        return this;
    }

    @Step("Fill edit painting form with new image")
    public EditPaintingPage fillEditFormWithImage(String title, String imagePath, String authorName, String description, String museumName) {
        setTitle(title);
        setImage(imagePath);
        selectAuthor(authorName);
        setDescription(description);
        selectMuseum(museumName);
        return this;
    }

    @Step("Click save button")
    public PaintingDetailsPage save() {
        saveButton.click();
        return new PaintingDetailsPage();
    }

    @Step("Click close button")
    public PaintingDetailsPage close() {
        closeButton.click();
        return new PaintingDetailsPage();
    }

    @Step("Check title field error: {expectedError}")
    public EditPaintingPage checkTitleError(String expectedError) {
        titleError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check author field error: {expectedError}")
    public EditPaintingPage checkAuthorError(String expectedError) {
        authorError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check description field error: {expectedError}")
    public EditPaintingPage checkDescriptionError(String expectedError) {
        descriptionError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check museum field error: {expectedError}")
    public EditPaintingPage checkMuseumError(String expectedError) {
        museumError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Check image field error: {expectedError}")
    public EditPaintingPage checkImageError(String expectedError) {
        imageError.shouldHave(text(expectedError));
        return this;
    }
}