package guru.qa.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaintingsPage extends BasePage<PaintingsPage> {

    private final SelenideElement pageTitle = $("h2.text-3xl");
    private final SelenideElement addPaintingButton = $$("button.btn").filter(text("Добавить картину")).first();
    private final SelenideElement searchInput = $("input.input[type='search']");
    private final SelenideElement searchButton = $("button.btn-icon");
    private final ElementsCollection paintingItems = $$("ul.grid li");

    public PaintingsPage checkPageTitle() {
        pageTitle.shouldHave(text("Картины"));
        return this;
    }

    public PaintingsPage searchPainting(String paintingName) {
        searchInput.setValue(paintingName);
        searchButton.click();
        return this;
    }

    public PaintingsPage checkPaintingExists(String paintingName) {
        paintingItems.findBy(text(paintingName)).shouldBe(visible);
        return this;
    }

    public PaintingsPage checkPaintingNotExists(String paintingName) {
        paintingItems.filter(text(paintingName)).shouldHave(CollectionCondition.size(0));
        return this;
    }

    public AddPaintingPage goToAddPainting() {
        addPaintingButton.click();
        return new AddPaintingPage();
    }

    public PaintingDetailsPage openPainting(String paintingName) {
        paintingItems.findBy(text(paintingName)).$("a").click();
        return new PaintingDetailsPage();
    }

    public int getPaintingsCount() {
        return paintingItems.size();
    }
}