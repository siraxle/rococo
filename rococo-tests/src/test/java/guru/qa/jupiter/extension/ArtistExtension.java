package guru.qa.jupiter.extension;

import guru.qa.jupiter.annotation.Artist;
import guru.qa.model.ArtistJson;
import guru.qa.service.api.ArtistApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private final ArtistApiClient artistApiClient = new ArtistApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(artistAnno -> {
                    String name = artistAnno.name().isEmpty()
                            ? RandomDataUtils.randomArtistName()
                            : artistAnno.name();
                    String biography = artistAnno.biography().isEmpty()
                            ? RandomDataUtils.randomBiography()
                            : artistAnno.biography();

                    ArtistJson artist = new ArtistJson(null, name, biography, null);
                    ArtistJson created = artistApiClient.createArtist(artist);
                    setArtist(created);
                });
    }

    public static void setArtist(ArtistJson artist) {
        context().getStore(NAMESPACE).put(context().getUniqueId(), artist);
    }

    public static Optional<ArtistJson> getArtist() {
        return Optional.ofNullable(context().getStore(NAMESPACE)
                .get(context().getUniqueId(), ArtistJson.class));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getArtist().orElseThrow(() -> new IllegalStateException("Artist not found in context"));
    }
}