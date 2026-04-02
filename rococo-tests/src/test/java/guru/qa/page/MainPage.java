package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

    private final SelenideElement welcomeText = $("p.text-3xl");
    private final SelenideElement paintingsLink = $("a[href='/painting']");
    private final SelenideElement artistsLink = $("a[href='/artist']");
    private final SelenideElement museumsLink = $("a[href='/museum']");

    @Step("Check welcome text: {expectedText} on MainPage")
    public MainPage checkWelcomeText(String expectedText) {
        welcomeText.shouldHave(text(expectedText));
        return this;
    }

    public PaintingsPage goToPaintings() {
        paintingsLink.click();
        return new PaintingsPage();
    }

    public ArtistsPage goToArtists() {
        artistsLink.click();
        return new ArtistsPage();
    }

    public MuseumsPage goToMuseums() {
        museumsLink.click();
        return new MuseumsPage();
    }
}