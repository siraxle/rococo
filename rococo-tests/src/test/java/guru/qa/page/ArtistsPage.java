package guru.qa.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ArtistsPage extends BasePage<ArtistsPage> {

    // Локаторы на основе HTML
    private final SelenideElement pageTitle = $x("//h2[contains(@class, 'text-3xl') and text()='Художники']");
    private final SelenideElement addArtistButton = $x("//button[contains(@class, 'btn') and contains(text(), 'Добавить художника')]");
    private final SelenideElement searchInput = $x("//input[@type='search' and @placeholder='Искать художников...']");
    private final SelenideElement searchButton = $x("//button[contains(@class, 'btn-icon')]//img[contains(@src, 'search')]/..");
    private final ElementsCollection artistItems = $$x("//ul[contains(@class, 'grid')]/li");

    @Step("Проверить заголовок страницы")
    public ArtistsPage checkPageTitle() {
        pageTitle.shouldBe(visible);
        return this;
    }

    @Step("Поиск художника: {artistName}")
    public ArtistsPage searchArtist(String artistName) {
        searchInput.setValue(artistName);
        searchButton.click();
        return this;
    }

    @Step("Проверить, что художник {artistName} существует")
    public ArtistsPage checkArtistExists(String artistName) {
        artistItems.findBy(text(artistName)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что художник {artistName} отсутствует")
    public ArtistsPage checkArtistNotExists(String artistName) {
        artistItems.filter(text(artistName)).shouldHave(CollectionCondition.size(0));
        return this;
    }

    @Step("Перейти к добавлению художника")
    public AddArtistPage goToAddArtist() {
        addArtistButton.click();
        return new AddArtistPage();
    }

    @Step("Открыть карточку художника {artistName}")
    public ArtistDetailsPage openArtist(String artistName) {
        artistItems.findBy(text(artistName)).$x(".//a").click();
        return new ArtistDetailsPage();
    }

    @Step("Получить количество художников в списке")
    public int getArtistsCount() {
        return artistItems.size();
    }
}