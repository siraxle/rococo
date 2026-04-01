package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class PaintingDetailsPage extends BasePage<PaintingDetailsPage> {

    // Основные элементы страницы
    private final SelenideElement paintingImage = $x("//article[contains(@class, 'card')]//img");
    private final SelenideElement paintingTitle = $x("//article[contains(@class, 'card')]//header[contains(@class, 'card-header') and contains(@class, 'font-bold')]");
    private final SelenideElement artistName = $x("//article[contains(@class, 'card')]//div[contains(@class, 'text-center')][not(contains(@class, 'card-header'))]");
    private final SelenideElement description = $x("//article[contains(@class, 'card')]//div[contains(@class, 'm-4')]");
    private final SelenideElement editButton = $x("//button[@data-testid='edit-painting']");

    @Step("Проверить название картины")
    public PaintingDetailsPage checkTitle(String expectedTitle) {
        paintingTitle.shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Проверить имя художника")
    public PaintingDetailsPage checkArtist(String expectedArtist) {
        artistName.shouldHave(text(expectedArtist));
        return this;
    }

    @Step("Проверить описание картины")
    public PaintingDetailsPage checkDescription(String expectedDescription) {
        description.shouldHave(text(expectedDescription));
        return this;
    }

    @Step("Проверить, что изображение загружено")
    public PaintingDetailsPage checkImageLoaded() {
        paintingImage.shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Редактировать'")
    public EditPaintingPage clickEdit() {
        editButton.click();
        return new EditPaintingPage();
    }

    @Step("Получить название картины")
    public String getTitle() {
        return paintingTitle.getText();
    }

    @Step("Получить имя художника")
    public String getArtist() {
        return artistName.getText();
    }

    @Step("Получить описание")
    public String getDescription() {
        return description.getText();
    }
}