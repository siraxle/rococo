package guru.qa.test.web;

import guru.qa.config.Config;
import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.Painting;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.WebTest;
import guru.qa.model.ArtistJson;
import guru.qa.model.MuseumJson;
import guru.qa.model.PaintingJson;
import guru.qa.model.UserJson;
import guru.qa.page.AddPaintingPage;
import guru.qa.page.LoginPage;
import guru.qa.page.PaintingDetailsPage;
import guru.qa.page.PaintingsPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@DisplayName("Web Painting Tests")
public class PaintingWebTest {

    private static final String TEST_IMAGE_PATH = "test-painting.jpg";
    private static final String DEFAULT_PASSWORD = "123456";
    private final Config CFG = Config.getInstance();

    @Test
    @User
    @Artist
    @Museum
    @DisplayName("Should create new painting")
    void userShouldCreateNewPainting(UserJson user, MuseumJson museum, ArtistJson artist) {
        String paintingTitle = "Test Painting " + System.currentTimeMillis();
        String paintingDescription = "Test painting description " + System.currentTimeMillis();

        PaintingsPage paintingsPage = loginAndGoToPaintings(user);
        AddPaintingPage addPaintingPage = paintingsPage.goToAddPainting();
        addPaintingPage.setTitle(paintingTitle)
                .setImage(TEST_IMAGE_PATH)
                .selectAuthor(artist.name())
                .setDescription(paintingDescription)
                .selectMuseum(museum.title())
                .submit()
                .checkToastMessage("Добавлена картины: " + paintingTitle);

        PaintingDetailsPage detailsPage = paintingsPage.openPainting(paintingTitle);
        detailsPage.checkTitle(paintingTitle)
                .checkArtist(artist.name())
                .checkDescription(paintingDescription)
                .checkImageLoaded();
    }

    @Test
    @User
    @Artist
    @Museum
    @Painting
    @DisplayName("Should search painting by title")
    void userShouldSearchPaintingByTitle(UserJson user,
                                         PaintingJson paintingJson) {
        String paintingTitle = paintingJson.title();

        PaintingsPage paintingsPage = loginAndGoToPaintings(user)
                .searchPainting(paintingTitle);

        paintingsPage.checkPaintingExists(paintingTitle);
    }

    @Test
    @User
    @DisplayName("Should not find non-existent painting")
    void userShouldNotFindNonExistentPainting(UserJson user) {
        String nonExistentPainting = "NonExistentPainting_" + System.currentTimeMillis();

        PaintingsPage paintingsPage = loginAndGoToPaintings(user)
                .searchPainting(nonExistentPainting);

        paintingsPage.checkPaintingNotExists(nonExistentPainting);
    }

    @Test
    @User
    @Artist
    @Museum
    @Painting
    @DisplayName("Should open painting details")
    void userShouldOpenPaintingDetails(UserJson user, PaintingJson paintingJson, ArtistJson artistJson) {
        String paintingTitle = paintingJson.title();

        PaintingDetailsPage detailsPage = loginAndGoToPaintings(user)
                .searchPainting(paintingTitle)
                .openPainting(paintingTitle);

        detailsPage.checkTitle(paintingTitle)
                .checkArtist(artistJson.name())
                .checkDescription(paintingJson.description())
                .checkImageLoaded();
    }

    @Test
    @User
    @Artist
    @Museum
    @Painting
    @DisplayName("Should edit existing painting")
    void userShouldEditExistingPainting(UserJson user, PaintingJson paintingJson, ArtistJson artistJson) {
        String originalTitle = paintingJson.title();
        String updatedTitle = originalTitle + "(Updated)";
        String updatedDescription = "Updated description for :" + paintingJson.title();

        PaintingsPage paintingsPage = loginAndGoToPaintings(user);

        PaintingDetailsPage detailsPage = paintingsPage
                .searchPainting(originalTitle)
                .openPainting(originalTitle);
        detailsPage.clickEdit()
                .setTitle(updatedTitle)
                .selectAuthor(artistJson.name())
                .setDescription(updatedDescription)
                .save()
                .checkToastMessage("Обновлена картина: " + updatedTitle)
                .goToPaintings();

        paintingsPage.searchPainting(updatedTitle);
        paintingsPage.checkPaintingExists(updatedTitle);

        PaintingDetailsPage updatedDetails = paintingsPage.openPainting(updatedTitle);
        updatedDetails.checkTitle(updatedTitle)
                .checkDescription(updatedDescription);
    }

    @Test
    @User
    @Artist
    @Museum
    @DisplayName("Should show error when creating painting without title")
    void userShouldSeeErrorWhenCreatingPaintingWithoutTitle(UserJson user,
                                                            ArtistJson artistJson,
                                                            MuseumJson museumJson) {
        String paintingDescription = "Test description without title";

        AddPaintingPage addPaintingPage = loginAndGoToPaintings(user)
                .goToAddPainting();

        addPaintingPage.setImage(TEST_IMAGE_PATH)
                .selectAuthor(artistJson.name())
                .setDescription(paintingDescription)
                .selectMuseum(museumJson.title())
                .submit()
                .checkPictureNameValidationMessage("Заполните это поле.");;
    }

    @Test
    @User
    @Artist
    @Museum
    @DisplayName("Should show error when creating painting without image")
    void userShouldSeeErrorWhenCreatingPaintingWithoutImage(UserJson user,
                                                            ArtistJson artistJson,
                                                            MuseumJson museumJson){
        String paintingTitle = "Painting Without Image " + System.currentTimeMillis();

        AddPaintingPage addPaintingPage = loginAndGoToPaintings(user)
                .goToAddPainting();

        addPaintingPage
                .setTitle(paintingTitle)
                .selectAuthor(artistJson.name())
                .setDescription("paintingDescription" + paintingTitle)
                .selectMuseum(museumJson.title())
                .submit()
                .checkImageInputValidationMessage("Выберите файл.");;
    }

    @Step("Login and navigate to paintings page")
    private PaintingsPage loginAndGoToPaintings(UserJson user) {
        return open(CFG.frontUrl() + "login", LoginPage.class)
                .goToLogin()
                .login(user.username(), user.testData().password())
                .goToPaintings();
    }
}