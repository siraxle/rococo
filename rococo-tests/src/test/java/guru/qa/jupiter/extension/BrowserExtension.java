package guru.qa.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private static final AtomicBoolean isConfigured = new AtomicBoolean(false);

    static {
        configureSelenide();
    }

    private static synchronized void configureSelenide() {
        if (!isConfigured.get()) {
            Configuration.browser = "chrome";
            Configuration.timeout = 8000;
            Configuration.pageLoadStrategy = "eager";

            if ("docker".equals(System.getProperty("test.env"))) {
                Configuration.remote = "http://selenoid:4444/wd/hub";
                Configuration.browserVersion = "127.0";
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("intl.accept_languages", "ru-RU,ru");
                Map<String, Object> selenoidOptions = new HashMap<>();
                selenoidOptions.put("env", new String[]{"LANG=ru_RU.UTF-8", "LANGUAGE=ru_RU:ru", "LC_ALL=ru_RU.UTF-8"});
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--lang=ru-RU");
                chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.setCapability("selenoid:options", selenoidOptions);
                Configuration.browserCapabilities = chromeOptions;
            }
            isConfigured.set(true);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(true)
                .screenshots(true)
                .includeSelenideSteps(true));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        throw throwable;
    }

    private static void doScreenshot() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment("Screen fail", new ByteArrayInputStream(
                    ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
            ));
        }
    }
}