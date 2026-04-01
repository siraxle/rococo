package guru.qa.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MuseumsPage extends BasePage<MuseumsPage> {

    // Заголовок страницы
    private final SelenideElement pageTitle = $x("//h2[contains(@class, 'text-3xl') and text()='Музеи']");

    // Кнопка добавления музея
    private final SelenideElement addMuseumButton = $x("//button[contains(@class, 'btn') and contains(text(), 'Добавить музей')]");

    // Поиск
    private final SelenideElement searchInput = $x("//input[@type='search' and @placeholder='Искать музей...']");
    private final SelenideElement searchButton = $x("//button[contains(@class, 'btn-icon')]//img[contains(@src, 'search')]/..");

    // Список музеев
    private final ElementsCollection museumItems = $$x("//ul[contains(@class, 'grid')]/li");

    // Элементы внутри карточки музея
    private final SelenideElement museumNameInCard = $x(".//div[contains(@class, 'text-center')][1]");
    private final SelenideElement museumLocationInCard = $x(".//div[contains(@class, 'text-center')][2]");
    private final SelenideElement museumImage = $x(".//img");

    @Step("Проверить заголовок страницы")
    public MuseumsPage checkPageTitle() {
        pageTitle.shouldBe(visible);
        return this;
    }

    @Step("Поиск музея: {museumName}")
    public MuseumsPage searchMuseum(String museumName) {
        searchInput.setValue(museumName);
        searchButton.click();
        return this;
    }

    @Step("Проверить, что музей {museumName} существует")
    public MuseumsPage checkMuseumExists(String museumName) {
        museumItems.findBy(text(museumName)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что музей {museumName} отсутствует")
    public MuseumsPage checkMuseumNotExists(String museumName) {
        museumItems.filter(text(museumName)).shouldHave(CollectionCondition.size(0));
        return this;
    }

    @Step("Перейти к добавлению музея")
    public AddMuseumPage goToAddMuseum() {
        addMuseumButton.click();
        return new AddMuseumPage();
    }

    @Step("Открыть карточку музея {museumName}")
    public MuseumDetailsPage openMuseum(String museumName) {
        museumItems.findBy(text(museumName)).$x(".//a").click();
        return new MuseumDetailsPage();
    }

    @Step("Получить количество музеев в списке")
    public int getMuseumsCount() {
        return museumItems.size();
    }

    @Step("Проверить информацию о музее в карточке")
    public MuseumsPage checkMuseumInfo(String museumName, String location) {
        SelenideElement museumCard = museumItems.findBy(text(museumName));
        museumCard.$x(".//div[contains(@class, 'text-center')][1]").shouldHave(text(museumName));
        museumCard.$x(".//div[contains(@class, 'text-center')][2]").shouldHave(text(location));
        return this;
    }

    @Step("Проверить, что у музея есть изображение")
    public MuseumsPage checkMuseumHasImage(String museumName) {
        museumItems.findBy(text(museumName)).$x(".//img").shouldBe(visible);
        return this;
    }
}