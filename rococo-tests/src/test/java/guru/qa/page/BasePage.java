package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    protected final SelenideElement snackBar = $(".MuiAlert-message");

    // Toast элемент для уведомлений
    protected final SelenideElement toast = $x("//div[@data-testid='toast']");
    protected final SelenideElement toastMessage = $x("//div[@data-testid='toast']//div[contains(@class, 'text-base')]");
    protected final SelenideElement toastCloseButton = $x("//div[@data-testid='toast']//button[@aria-label='Dismiss toast']");

    // Navigation menu elements
    protected final SelenideElement paintingsLink = $x("//nav//a[@href='/painting']");
    protected final SelenideElement artistsLink = $x("//nav//a[@href='/artist']");
    protected final SelenideElement museumsLink = $x("//nav//a[@href='/museum']");

    @SuppressWarnings("unchecked")
    public T checkSnackbarText(String expectedText) {
        snackBar.shouldHave(text(expectedText));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T checkSnackbarContainsText(String expectedText) {
        snackBar.shouldHave(text(expectedText));
        return (T) this;
    }

    @Step("Check toast message: {expectedText}")
    @SuppressWarnings("unchecked")
    public T checkToastMessage(String expectedText) {
        toast.shouldBe(visible);
        toastMessage.shouldHave(text(expectedText));
        return (T) this;
    }

    @Step("Close toast")
    @SuppressWarnings("unchecked")
    public T closeToast() {
        toastCloseButton.click();
        return (T) this;
    }

    // Navigation methods
    @Step("Navigate to Paintings page")
    public PaintingsPage goToPaintings() {
        paintingsLink.click();
        return new PaintingsPage();
    }

    @Step("Navigate to Artists page")
    public ArtistsPage goToArtists() {
        artistsLink.click();
        return new ArtistsPage();
    }

    @Step("Navigate to Museums page")
    public MuseumsPage goToMuseums() {
        museumsLink.click();
        return new MuseumsPage();
    }
}