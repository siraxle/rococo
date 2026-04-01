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

    @Step("Проверить заголовок модального окна")
    public EditPaintingPage checkModalTitle() {
        modalTitle.shouldBe(visible);
        return this;
    }

    @Step("Проверить текущее изображение")
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

    @Step("Обновить изображение: {imagePath}")
    public EditPaintingPage setImage(String imagePath) {
        imageInput.uploadFile(new File(imagePath));
        return this;
    }

    @Step("Установить название картины: {title}")
    public EditPaintingPage setTitle(String title) {
        titleInput.clear();
        titleInput.setValue(title);
        return this;
    }

    @Step("Выбрать автора: {authorName}")
    public EditPaintingPage selectAuthor(String authorName) {
        authorSelect.selectOptionContainingText(authorName);
        return this;
    }

    @Step("Установить описание: {description}")
    public EditPaintingPage setDescription(String description) {
        descriptionTextarea.clear();
        descriptionTextarea.setValue(description);
        return this;
    }

    @Step("Выбрать музей: {museumName}")
    public EditPaintingPage selectMuseum(String museumName) {
        museumSelect.selectOptionContainingText(museumName);
        return this;
    }

    @Step("Заполнить форму редактирования картины")
    public EditPaintingPage fillEditForm(String title, String authorName, String description, String museumName) {
        setTitle(title);
        selectAuthor(authorName);
        setDescription(description);
        selectMuseum(museumName);
        return this;
    }

    @Step("Заполнить форму редактирования картины с обновлением изображения")
    public EditPaintingPage fillEditFormWithImage(String title, String imagePath, String authorName, String description, String museumName) {
        setTitle(title);
        setImage(imagePath);
        selectAuthor(authorName);
        setDescription(description);
        selectMuseum(museumName);
        return this;
    }

    @Step("Нажать кнопку 'Сохранить'")
    public PaintingDetailsPage save() {
        saveButton.click();
        return new PaintingDetailsPage();
    }

    @Step("Нажать кнопку 'Закрыть'")
    public PaintingDetailsPage close() {
        closeButton.click();
        return new PaintingDetailsPage();
    }

    @Step("Проверить ошибку поля 'Название картины'")
    public EditPaintingPage checkTitleError(String expectedError) {
        titleError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Автор'")
    public EditPaintingPage checkAuthorError(String expectedError) {
        authorError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Описание'")
    public EditPaintingPage checkDescriptionError(String expectedError) {
        descriptionError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Музей'")
    public EditPaintingPage checkMuseumError(String expectedError) {
        museumError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Изображение'")
    public EditPaintingPage checkImageError(String expectedError) {
        imageError.shouldHave(text(expectedError));
        return this;
    }
}