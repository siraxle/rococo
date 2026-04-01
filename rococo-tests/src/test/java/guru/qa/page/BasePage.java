package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    protected final SelenideElement snackBar = $(".MuiAlert-message");

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
}