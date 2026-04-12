package guru.qa.test.web;

import guru.qa.config.Config;
import guru.qa.jupiter.annotation.Artist;
import guru.qa.jupiter.annotation.User;
import guru.qa.jupiter.annotation.meta.WebTest;
import guru.qa.model.ArtistJson;
import guru.qa.model.UserJson;
import guru.qa.page.AddArtistPage;
import guru.qa.page.ArtistDetailsPage;
import guru.qa.page.ArtistsPage;
import guru.qa.page.LoginPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@WebTest
@DisplayName("Web Artist Tests")
public class ArtistWebTest {

    private static final String TEST_IMAGE_PATH = "test-painting.jpg";
    private final Config CFG = Config.getInstance();

    @Test
    @User
    @DisplayName("Should create new artist")
    void userShouldCreateNewArtist(UserJson user) {
        String artistName = "Test Artist " + System.currentTimeMillis();
        String artistBiography = "Test artist biography " + System.currentTimeMillis();

        ArtistsPage artistsPage = loginAndGoToArtists(user);

        AddArtistPage addArtistPage = artistsPage.goToAddArtist();
        addArtistPage.setName(artistName)
                .setPhoto(TEST_IMAGE_PATH)
                .setBiography(artistBiography)
                .submitAndGoToArtists()
                .checkToastMessage("Добавлен художник: " + artistName);

        artistsPage.searchArtist(artistName);
        artistsPage.checkArtistExists(artistName);
    }

    @Test
    @User
    @Artist
    @DisplayName("Should search artist by name")
    void userShouldSearchArtistByName(UserJson user, ArtistJson artistJson) {
        String artistName = artistJson.name();

        ArtistsPage artistsPage = loginAndGoToArtists(user)
                .searchArtist(artistName);

        artistsPage.checkArtistExists(artistName);
    }

    @Test
    @User
    @DisplayName("Should not find non-existent artist")
    void userShouldNotFindNonExistentArtist(UserJson user) {
        String nonExistentArtist = "NonExistentArtist_" + System.currentTimeMillis();

        ArtistsPage artistsPage = loginAndGoToArtists(user)
                .searchArtist(nonExistentArtist);

        artistsPage.checkArtistNotExists(nonExistentArtist);
    }

    @Test
    @User
    @Artist
    @DisplayName("Should open artist details")
    void userShouldOpenArtistDetails(UserJson user, ArtistJson artistJson) {
        String artistName = artistJson.name();

        ArtistDetailsPage detailsPage = loginAndGoToArtists(user)
                .openArtist(artistName);

        detailsPage.checkPageTitle(artistName);
    }

    @Test
    @User
    @DisplayName("Should show error when creating artist without name")
    void userShouldSeeErrorWhenCreatingArtistWithoutName(UserJson user) {
        String artistBiography = "Test biography without name";

        AddArtistPage addArtistPage = loginAndGoToArtists(user)
                .goToAddArtist();

        addArtistPage.setPhoto(TEST_IMAGE_PATH)
                .setBiography(artistBiography)
                .submit()
                .checkNameValidationMessage("Заполните это поле.");
    }

    @Test
    @User
    @DisplayName("Should show error when creating artist without biography")
    void userShouldSeeErrorWhenCreatingArtistWithoutBiography(UserJson user) {
        String artistName = "Test Artist " + System.currentTimeMillis();

        AddArtistPage addArtistPage = loginAndGoToArtists(user)
                .goToAddArtist();

        addArtistPage.setName(artistName)
                .setPhoto(TEST_IMAGE_PATH)
                .submit()
                .checkBiographyValidationMessage("Заполните это поле.");;
    }

    @Test
    @User
    @DisplayName("Should show error when creating artist without image")
    void userShouldSeeErrorWhenCreatingArtistWithoutImage(UserJson user) {
        String artistName = "Test Artist " + System.currentTimeMillis();
        String artistBiography = "Test biography without image";

        AddArtistPage addArtistPage = loginAndGoToArtists(user)
                .goToAddArtist();

        addArtistPage.setName(artistName)
                .setBiography(artistBiography)
                .submit()
                .checkPhotoValidationMessage("Выберите файл.");
    }

    @Step("Login and navigate to artists page")
    private ArtistsPage loginAndGoToArtists(UserJson user) {
        return open(CFG.frontUrl() + "login", LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToArtists();
    }
}