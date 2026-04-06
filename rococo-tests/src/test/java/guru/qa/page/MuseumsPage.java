package guru.qa.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MuseumsPage extends BasePage<MuseumsPage> {

    private final SelenideElement pageTitle = $x("//h2[contains(@class, 'text-3xl') and text()='Музеи']");
    private final SelenideElement addMuseumButton = $x("//button[contains(@class, 'btn') and contains(text(), 'Добавить музей')]");
    private final SelenideElement searchInput = $x("//input[@type='search' and @placeholder='Искать музей...']");
    private final SelenideElement searchButton = $x("//button[contains(@class, 'btn-icon')]//img[contains(@src, 'search')]/..");
    private final ElementsCollection museumItems = $$x("//ul[contains(@class, 'grid')]/li");

    private final String museumNameXpath = ".//div[contains(@class, 'text-center')][1]";
    private final String museumLocationXpath = ".//div[contains(@class, 'text-center')][2]";
    private final String museumImageXpath = ".//img";
    private final String museumLinkXpath = ".//a";

    @Step("Check page title")
    public MuseumsPage checkPageTitle() {
        pageTitle.shouldBe(visible);
        return this;
    }

    @Step("Search museum: {museumName}")
    public MuseumsPage searchMuseum(String museumName) {
        searchInput.setValue(museumName);
        searchButton.click();
        return this;
    }

    @Step("Check that museum {museumName} exists")
    public MuseumsPage checkMuseumExists(String museumName) {
        museumItems.findBy(text(museumName)).shouldBe(visible);
        return this;
    }

    @Step("Check that museum {museumName} does not exist")
    public MuseumsPage checkMuseumNotExists(String museumName) {
        museumItems.filter(text(museumName)).shouldHave(CollectionCondition.size(0));
        return this;
    }

    @Step("Navigate to add museum page")
    public AddMuseumPage goToAddMuseum() {
        addMuseumButton.click();
        return new AddMuseumPage();
    }

    @Step("Open museum details: {museumName}")
    public MuseumDetailsPage openMuseum(String museumName) {
        museumItems.findBy(text(museumName)).$x(museumLinkXpath).click();
        return new MuseumDetailsPage();
    }

    @Step("Get museums count")
    public int getMuseumsCount() {
        return museumItems.size();
    }

    @Step("Check museum info: name={museumName}, location={location}")
    public MuseumsPage checkMuseumInfo(String museumName, String location) {
        SelenideElement museumCard = museumItems.findBy(text(museumName));
        museumCard.$x(museumNameXpath).shouldHave(text(museumName));
        museumCard.$x(museumLocationXpath).shouldHave(text(location));
        return this;
    }

    @Step("Check that museum {museumName} has image")
    public MuseumsPage checkMuseumHasImage(String museumName) {
        museumItems.findBy(text(museumName)).$x(museumImageXpath).shouldBe(visible);
        return this;
    }
}