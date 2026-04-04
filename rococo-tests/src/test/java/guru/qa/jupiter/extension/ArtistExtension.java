package guru.qa.jupiter.extension;

import guru.qa.config.DatabaseConfig;
import guru.qa.jupiter.annotation.Artist;
import guru.qa.model.ArtistJson;
import guru.qa.service.ArtistClient;
import guru.qa.service.api.ArtistApiClient;
import guru.qa.service.db.ArtistDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class ArtistExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private final ArtistClient artistClient;

    public ArtistExtension() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DatabaseConfig.class);
        // Переключение между API и DB клиентом
        // this.artistClient = new ArtistApiClient();                                    // API client
        this.artistClient = applicationContext.getBean(ArtistDbClient.class);          // DB client
    }

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

                    // Генерируем UUID для художника
                    String artistId = UUID.randomUUID().toString();
                    ArtistJson artist = new ArtistJson(artistId, name, biography, null);

                    ArtistJson created = artistClient.createArtist(artist);
                    System.out.println("Created artist: " + created);
                    setArtist(created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getArtist().ifPresent(artist -> {
            try {
                artistClient.deleteArtist(artist.id());
            } catch (Exception e) {
                System.err.println("Failed to delete artist: " + artist.id() + ", error: " + e.getMessage());
            }
        });
        context.getStore(NAMESPACE).remove(context.getUniqueId());
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