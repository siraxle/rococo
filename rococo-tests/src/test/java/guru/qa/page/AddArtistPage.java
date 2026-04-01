package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class AddArtistPage extends BasePage<AddArtistPage> {

    // Заголовок модального окна
    private final SelenideElement modalTitle = $x("//div[contains(@class, 'card')]//header[contains(@class, 'text-2xl') and contains(text(), 'Новый художник')]");

    // Поля формы
    private final SelenideElement nameInput = $x("//input[@name='name']");
    private final SelenideElement photoInput = $x("//input[@name='photo']");
    private final SelenideElement biographyTextarea = $x("//textarea[@name='biography']");

    // Кнопки
    private final SelenideElement closeButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-ringed') and contains(text(), 'Закрыть')]");
    private final SelenideElement submitButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-filled-primary') and contains(text(), 'Добавить')]");

    // Ошибки
    private final SelenideElement nameError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Имя']]");
    private final SelenideElement photoError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Изображение художника']]");
    private final SelenideElement biographyError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Биография']]");

    @Step("Проверить заголовок модального окна")
    public AddArtistPage checkModalTitle() {
        modalTitle.shouldBe(visible);
        return this;
    }

    @Step("Установить имя художника: {name}")
    public AddArtistPage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Step("Загрузить изображение художника: {imagePath}")
    public AddArtistPage setPhoto(String imagePath) {
        photoInput.uploadFile(new File(imagePath));
        return this;
    }

    @Step("Установить биографию: {biography}")
    public AddArtistPage setBiography(String biography) {
        biographyTextarea.setValue(biography);
        return this;
    }

    @Step("Заполнить форму добавления художника")
    public AddArtistPage fillArtistForm(String name, String imagePath, String biography) {
        setName(name);
        setPhoto(imagePath);
        setBiography(biography);
        return this;
    }

    @Step("Нажать кнопку 'Добавить'")
    public ArtistsPage submit() {
        submitButton.click();
        return new ArtistsPage();
    }

    @Step("Нажать кнопку 'Закрыть'")
    public ArtistsPage close() {
        closeButton.click();
        return new ArtistsPage();
    }

    @Step("Проверить ошибку поля 'Имя'")
    public AddArtistPage checkNameError(String expectedError) {
        nameError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Изображение'")
    public AddArtistPage checkPhotoError(String expectedError) {
        photoError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Биография'")
    public AddArtistPage checkBiographyError(String expectedError) {
        biographyError.shouldHave(text(expectedError));
        return this;
    }
}