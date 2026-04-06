package guru.qa.jupiter.extension;

import guru.qa.config.ApplicationContextHolder;
import guru.qa.jupiter.annotation.Museum;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.CountryJson;
import guru.qa.model.GeoJson;
import guru.qa.model.MuseumJson;
import guru.qa.service.MuseumClient;
import guru.qa.service.api.CountryApiClient;
import guru.qa.service.api.MuseumApiClient;
import guru.qa.service.db.GeoDbClient;
import guru.qa.service.db.MuseumDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class MuseumExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private static final String DEFAULT_IMAGE_PATH = "images/test-painting.jpg";

    private final MuseumClient dbClient;
    private final MuseumApiClient apiClient;
    private final GeoDbClient geoDbClient;
    private final CountryApiClient countryApiClient;

    public MuseumExtension() {
        ApplicationContext applicationContext = ApplicationContextHolder.getContext();
        this.dbClient = applicationContext.getBean(MuseumDbClient.class);
        this.apiClient = new MuseumApiClient();
        this.geoDbClient = applicationContext.getBean(GeoDbClient.class);
        this.countryApiClient = new CountryApiClient();
    }

    private boolean isApiTest(ExtensionContext context) {
        return context.getTestMethod()
                .map(method -> method.isAnnotationPresent(RestTest.class))
                .orElse(false) ||
                context.getTestClass()
                        .map(clazz -> clazz.isAnnotationPresent(RestTest.class))
                        .orElse(false);
    }

    private MuseumClient getClient(ExtensionContext context) {
        return isApiTest(context) ? apiClient : dbClient;
    }

    private CountryJson getCountry(ExtensionContext context, String countryId) {
        if (isApiTest(context)) {
            if (countryId != null && !countryId.isEmpty()) {
                return countryApiClient.getCountry(countryId);
            } else {
                return countryApiClient.getAllCountries().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No countries found"));
            }
        } else {
            if (countryId != null && !countryId.isEmpty()) {
                return geoDbClient.getCountry(countryId);
            } else {
                return geoDbClient.getAllCountries().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No countries found"));
            }
        }
    }

    private String getTestImagePath() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(DEFAULT_IMAGE_PATH);
        if (resource == null) {
            throw new IllegalArgumentException("Test image not found: " + DEFAULT_IMAGE_PATH);
        }
        return resource.getPath();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museumAnno -> {
                    String title = museumAnno.title().isEmpty()
                            ? RandomDataUtils.randomMuseumTitle()
                            : museumAnno.title();
                    String city = museumAnno.city().isEmpty()
                            ? RandomDataUtils.randomCity()
                            : museumAnno.city();
                    String countryId = museumAnno.countryId().isEmpty() ? null : museumAnno.countryId();

                    CountryJson country = getCountry(context, countryId);

                    if (country.id() == null || country.id().isEmpty()) {
                        throw new IllegalStateException("Country ID is null or empty for country: " + country);
                    }

                    String museumId = UUID.randomUUID().toString();
                    GeoJson geo = new GeoJson(city, country);

                    String testImagePath = getTestImagePath();

                    MuseumJson museum = new MuseumJson(
                            museumId,
                            title,
                            RandomDataUtils.randomDescription(),
                            city,
                            RandomDataUtils.randomAddress(),
                            testImagePath,
                            geo
                    );

                    MuseumClient client = getClient(context);
                    MuseumJson created;

                    if (isApiTest(context)) {
                        created = client.createMuseum(museum);
                    } else {
                        created = ((MuseumDbClient) client).createMuseum(museum, country.id());
                    }

                    System.out.println("Created museum via " + (isApiTest(context) ? "API" : "DB") + ": " + created);
                    setMuseum(created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getMuseum().ifPresent(museum -> {
            try {
                MuseumClient client = getClient(context);
                client.deleteMuseum(museum.id());
                System.out.println("Deleted museum: " + museum.id());
            } catch (Exception e) {
                System.err.println("Failed to delete museum: " + museum.id() + ", error: " + e.getMessage());
            }
        });
        context.getStore(NAMESPACE).remove(context.getUniqueId());
    }

    public static void setMuseum(MuseumJson museum) {
        context().getStore(NAMESPACE).put(context().getUniqueId(), museum);
    }

    public static Optional<MuseumJson> getMuseum() {
        return Optional.ofNullable(context().getStore(NAMESPACE)
                .get(context().getUniqueId(), MuseumJson.class));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public MuseumJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getMuseum().orElseThrow(() -> new IllegalStateException("Museum not found in context"));
    }
}