package guru.qa.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class MuseumDetailsPage extends BasePage<MuseumDetailsPage> {

    // Основные элементы страницы
    private final SelenideElement museumTitle = $x("//article[contains(@class, 'card')]//header[contains(@class, 'card-header') and contains(@class, 'font-bold')]");
    private final SelenideElement museumLocation = $x("//article[contains(@class, 'card')]//div[contains(@class, 'text-center')][not(contains(@class, 'card-header'))]");
    private final SelenideElement museumDescription = $x("//article[contains(@class, 'card')]//div[contains(@class, 'm-4') and not(contains(@class, 'w-56'))]");
    private final SelenideElement museumImage = $x("//article[contains(@class, 'card')]//img");
    private final SelenideElement editButton = $x("//div[contains(@class, 'w-56')]//button[contains(@class, 'btn')]");

    @Step("Проверить название музея")
    public MuseumDetailsPage checkTitle(String expectedTitle) {
        museumTitle.shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Проверить местоположение музея (страна, город)")
    public MuseumDetailsPage checkLocation(String expectedLocation) {
        museumLocation.shouldHave(text(expectedLocation));
        return this;
    }

    @Step("Проверить описание музея")
    public MuseumDetailsPage checkDescription(String expectedDescription) {
        museumDescription.shouldHave(text(expectedDescription));
        return this;
    }

    @Step("Проверить, что изображение музея загружено")
    public MuseumDetailsPage checkImageLoaded() {
        museumImage.shouldBe(visible);
        return this;
    }

//    @Step("Проверить, что изображение музея имеет src: {expectedSrc}")
//    public MuseumDetailsPage checkImageSrc(String expectedSrc) {
//        museumImage.shouldHave(org.openqa.selenium.By.tagName("img"),
//                new com.codeborne.selenide.Condition("src") {
//                    @Override
//                    public boolean apply(com.codeborne.selenide.Driver driver, org.openqa.selenium.WebElement element) {
//                        String src = element.getAttribute("src");
//                        return src != null && src.contains(expectedSrc);
//                    }
//                });
//        return this;
//    }

    @Step("Получить название музея")
    public String getTitle() {
        return museumTitle.getText();
    }

    @Step("Получить местоположение музея")
    public String getLocation() {
        return museumLocation.getText();
    }

    @Step("Получить описание музея")
    public String getDescription() {
        return museumDescription.getText();
    }
}