package guru.qa.test.web;

import guru.qa.config.Config;
import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.WebTest;
import guru.qa.model.MuseumJson;
import guru.qa.model.UserJson;
import guru.qa.page.AddMuseumPage;
import guru.qa.page.LoginPage;
import guru.qa.page.MuseumDetailsPage;
import guru.qa.page.MuseumsPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@DisplayName("Web Museum Tests")
public class MuseumWebTest {

    private static final String TEST_IMAGE_PATH = "test-painting.jpg";
    private final Config CFG = Config.getInstance();

    @Test
    @User
    @DisplayName("Should create new museum")
    void userShouldCreateNewMuseum(UserJson user) {
        String museumTitle = "Test Museum " + System.currentTimeMillis();
        String museumCity = "Test City " + System.currentTimeMillis();
        String museumDescription = "Test museum description " + System.currentTimeMillis();

        MuseumsPage museumsPage = loginAndGoToMuseums(user);

        AddMuseumPage addMuseumPage = museumsPage.goToAddMuseum();
        addMuseumPage.setTitle(museumTitle)
                .selectCountry("Болгария")
                .setCity(museumCity)
                .setPhoto(TEST_IMAGE_PATH)
                .setDescription(museumDescription)
                .submitAndGoToMuseums()
                .checkToastMessage("Добавлен музей: " + museumTitle);;

        MuseumDetailsPage detailsPage = museumsPage.openMuseum(museumTitle);
        detailsPage.checkTitle(museumTitle)
                .checkDescription(museumDescription);
    }

    @Test
    @User
    @Museum
    @DisplayName("Should search museum by title")
    void userShouldSearchMuseumByTitle(UserJson user, MuseumJson museumJson) {
        String museumTitle = museumJson.title();

        MuseumsPage museumsPage = loginAndGoToMuseums(user)
                .searchMuseum(museumTitle);

        museumsPage.checkMuseumExists(museumTitle);
    }

    @Test
    @User
    @DisplayName("Should not find non-existent museum")
    void userShouldNotFindNonExistentMuseum(UserJson user) {
        String nonExistentMuseum = "NonExistentMuseum_" + System.currentTimeMillis();

        MuseumsPage museumsPage = loginAndGoToMuseums(user)
                .searchMuseum(nonExistentMuseum);

        museumsPage.checkMuseumNotExists(nonExistentMuseum);
    }

    @Test
    @User
    @Museum
    @DisplayName("Should open museum details")
    void userShouldOpenMuseumDetails(UserJson user, MuseumJson museumJson) {
        String museumTitle = museumJson.title();

        MuseumDetailsPage detailsPage = loginAndGoToMuseums(user)
                .openMuseum(museumTitle);

        detailsPage.checkTitle(museumTitle)
                .checkLocation(museumJson.city())
                .checkImageLoaded();
    }

    @Test
    @User
    @DisplayName("Should show error when creating museum without title")
    void userShouldSeeErrorWhenCreatingMuseumWithoutTitle(UserJson user) {
        String museumCity = "Test City";
        String museumDescription = "Test description without title";

        AddMuseumPage addMuseumPage = loginAndGoToMuseums(user)
                .goToAddMuseum();

        addMuseumPage.selectCountry("Болгария")
                .setCity(museumCity)
                .setPhoto(TEST_IMAGE_PATH)
                .setDescription(museumDescription)
                .submit()
                .checkTitleValidationMessage("Заполните это поле.");
    }

    @Test
    @User
    @DisplayName("Should show error when creating museum without city")
    void userShouldSeeErrorWhenCreatingMuseumWithoutCity(UserJson user) {
        String museumTitle = "Test Museum " + System.currentTimeMillis();
        String museumDescription = "Test description without city";

        AddMuseumPage addMuseumPage = loginAndGoToMuseums(user)
                .goToAddMuseum();

        addMuseumPage.setTitle(museumTitle).selectCountry("Болгария")
                .setPhoto(TEST_IMAGE_PATH)
                .setDescription(museumDescription)
                .submit()
                .checkCityValidationMessage("Заполните это поле.");
    }

    @Test
    @User
    @DisplayName("Should show error when creating museum without description")
    void userShouldSeeErrorWhenCreatingMuseumWithoutDescription(UserJson user) {
        String museumTitle = "Test Museum " + System.currentTimeMillis();
        String museumCity = "Test City";

        AddMuseumPage addMuseumPage = loginAndGoToMuseums(user)
                .goToAddMuseum();

        addMuseumPage.setTitle(museumTitle)
                .setCity(museumCity)
                .setPhoto(TEST_IMAGE_PATH)
                .submit()
                .checkDescriptionValidationMessage("Заполните это поле.");
    }

    @Test
    @User
    @DisplayName("Should show error when creating museum without image")
    void userShouldSeeErrorWhenCreatingMuseumWithoutImage(UserJson user) {
        String museumTitle = "Test Museum " + System.currentTimeMillis();
        String museumCity = "Test City";
        String museumDescription = "Test description without image";

        AddMuseumPage addMuseumPage = loginAndGoToMuseums(user)
                .goToAddMuseum();

        addMuseumPage.setTitle(museumTitle)
                .setCity(museumCity)
                .setDescription(museumDescription)
                .submit()
                .checkPhotoValidationMessage("Выберите файл.");

    }

    @Step("Login and navigate to museums page")
    private MuseumsPage loginAndGoToMuseums(UserJson user) {
        return open(CFG.frontUrl() + "login", LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToMuseums();
    }
}