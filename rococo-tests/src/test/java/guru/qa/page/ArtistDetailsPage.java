package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ArtistDetailsPage extends BasePage<ArtistDetailsPage> {

    // Заголовок модального окна (для добавления/редактирования)
    private final SelenideElement modalTitle = $x("(//header[contains(@class, card-header)])[2]");

    // Поля формы
    private final SelenideElement nameInput = $x("//input[@name='name']");
    private final SelenideElement photoInput = $x("//input[@name='photo']");
    private final SelenideElement biographyTextarea = $x("//textarea[@name='biography']");

    // Кнопки
    private final SelenideElement closeButton = $x("//button[contains(@class, 'btn') and contains(text(), 'Закрыть')]");
    private final SelenideElement submitButton = $x("//button[contains(@class, 'btn') and contains(text(), 'Добавить')]");

    // Ошибки
    private final SelenideElement nameError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Имя']]");
    private final SelenideElement photoError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Изображение художника']]");
    private final SelenideElement biographyError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Биография']]");

    @Step("Проверить заголовок модального окна")
    public ArtistDetailsPage checkPageTitle(String expectedTitle) {
        modalTitle.shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Установить имя художника: {name}")
    public ArtistDetailsPage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Step("Загрузить изображение: {imagePath}")
    public ArtistDetailsPage setPhoto(String imagePath) {
        photoInput.uploadFile(new File(imagePath));
        return this;
    }

    @Step("Установить биографию: {biography}")
    public ArtistDetailsPage setBiography(String biography) {
        biographyTextarea.setValue(biography);
        return this;
    }

    @Step("Заполнить форму художника")
    public ArtistDetailsPage fillArtistForm(String name, String imagePath, String biography) {
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
    public ArtistDetailsPage checkNameError(String expectedError) {
        nameError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Изображение'")
    public ArtistDetailsPage checkPhotoError(String expectedError) {
        photoError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Биография'")
    public ArtistDetailsPage checkBiographyError(String expectedError) {
        biographyError.shouldHave(text(expectedError));
        return this;
    }
}