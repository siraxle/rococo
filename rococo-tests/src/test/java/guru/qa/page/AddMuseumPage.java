package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$x;

public class AddMuseumPage extends BasePage<AddMuseumPage> {

    // Заголовок модального окна
    private final SelenideElement modalTitle = $x("//div[contains(@class, 'card')]//header[contains(@class, 'text-2xl')]");

    // Поля формы
    private final SelenideElement titleInput = $x("//input[@name='title']");
    private final SelenideElement countrySelect = $x("//select[@name='countryId']");
    private final SelenideElement cityInput = $x("//input[@name='city']");
    private final SelenideElement photoInput = $x("//input[@name='photo']");
    private final SelenideElement descriptionTextarea = $x("//textarea[@name='description']");

    // Кнопки
    private final SelenideElement closeButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-ringed') and contains(text(), 'Закрыть')]");
    private final SelenideElement submitButton = $x("//div[contains(@class, 'text-right')]//button[contains(@class, 'variant-filled-primary') and contains(text(), 'Добавить')]");

    // Ошибки
    private final SelenideElement titleError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Название музея']]");
    private final SelenideElement countryError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Укажите страну']]");
    private final SelenideElement cityError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Укажите город']]");
    private final SelenideElement photoError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='Изображение музея']]");
    private final SelenideElement descriptionError = $x("//label//span[contains(@class, 'text-error-400') and preceding-sibling::span[text()='О музее']]");

    @Step("Проверить заголовок модального окна")
    public AddMuseumPage checkModalTitle(String expectedTitle) {
        modalTitle.shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Установить название музея: {title}")
    public AddMuseumPage setTitle(String title) {
        titleInput.setValue(title);
        return this;
    }

    @Step("Выбрать страну по тексту: {countryName}")
    public AddMuseumPage selectCountry(String countryName) {
        countrySelect.selectOptionContainingText(countryName);
        return this;
    }

    @Step("Установить город: {city}")
    public AddMuseumPage setCity(String city) {
        cityInput.setValue(city);
        return this;
    }

    @Step("Загрузить изображение музея: {imagePath}")
    public AddMuseumPage setPhoto(String imagePath) {
        photoInput.uploadFile(new File(imagePath));
        return this;
    }

    @Step("Установить описание музея: {description}")
    public AddMuseumPage setDescription(String description) {
        descriptionTextarea.setValue(description);
        return this;
    }

    @Step("Заполнить форму добавления музея")
    public AddMuseumPage fillMuseumForm(String title, String countryName, String city, String imagePath, String description) {
        setTitle(title);
        selectCountry(countryName);
        setCity(city);
        setPhoto(imagePath);
        setDescription(description);
        return this;
    }

    @Step("Нажать кнопку 'Добавить'")
    public MuseumsPage submit() {
        submitButton.click();
        return new MuseumsPage();
    }

    @Step("Нажать кнопку 'Закрыть'")
    public MuseumsPage close() {
        closeButton.click();
        return new MuseumsPage();
    }

    @Step("Проверить ошибку поля 'Название музея'")
    public AddMuseumPage checkTitleError(String expectedError) {
        titleError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Страна'")
    public AddMuseumPage checkCountryError(String expectedError) {
        countryError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Город'")
    public AddMuseumPage checkCityError(String expectedError) {
        cityError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'Изображение'")
    public AddMuseumPage checkPhotoError(String expectedError) {
        photoError.shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку поля 'О музее'")
    public AddMuseumPage checkDescriptionError(String expectedError) {
        descriptionError.shouldHave(text(expectedError));
        return this;
    }
}