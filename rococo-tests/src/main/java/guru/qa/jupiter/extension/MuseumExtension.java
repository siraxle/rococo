package guru.qa.jupiter.extension;

import guru.qa.jupiter.annotation.Museum;
import guru.qa.model.*;
import guru.qa.service.api.CountryApiClient;
import guru.qa.service.api.MuseumApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);

    private final MuseumApiClient museumApiClient = new MuseumApiClient();
    private final CountryApiClient countryApiClient = new CountryApiClient();

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

                    CountryJson country;
                    if (!museumAnno.countryId().isEmpty()) {
                        country = countryApiClient.getCountry(museumAnno.countryId());
                    } else {
                        country = countryApiClient.getAllCountries().stream()
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("No countries found"));
                    }

                    GeoJson geo = new GeoJson(city, country);
                    MuseumJson museum = new MuseumJson(null, title, RandomDataUtils.randomDescription(),
                            city, RandomDataUtils.randomAddress(), null, geo);

                    MuseumJson created = museumApiClient.createMuseum(museum);
                    setMuseum(created);
                });
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