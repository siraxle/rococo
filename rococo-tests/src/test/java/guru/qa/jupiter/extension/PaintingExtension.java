package guru.qa.jupiter.extension;

import guru.qa.config.ApplicationContextHolder;
import guru.qa.jupiter.annotation.Painting;
import guru.qa.jupiter.annotation.meta.RestTest;
import guru.qa.model.ArtistJson;
import guru.qa.model.MuseumJson;
import guru.qa.model.PaintingJson;
import guru.qa.service.PaintingClient;
import guru.qa.service.api.PaintingApiClient;
import guru.qa.service.db.PaintingDbClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class PaintingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);
    private static final String DEFAULT_IMAGE_PATH = "/images/test-painting.jpg";

    private final PaintingClient paintingClient;

    public PaintingExtension() {
        ApplicationContext applicationContext = ApplicationContextHolder.getContext();
        this.paintingClient = applicationContext.getBean(PaintingDbClient.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(paintingAnno -> {
                    String title = paintingAnno.title().isEmpty()
                            ? RandomDataUtils.randomPaintingTitle()
                            : paintingAnno.title();
                    String description = paintingAnno.description().isEmpty()
                            ? RandomDataUtils.randomDescription()
                            : paintingAnno.description();

                    ArtistJson artist = ArtistExtension.getArtist()
                            .orElseThrow(() -> new IllegalStateException("Artist not found in context. Use @Artist annotation."));
                    MuseumJson museum = MuseumExtension.getMuseum()
                            .orElseThrow(() -> new IllegalStateException("Museum not found in context. Use @Museum annotation."));

                    if (artist.id() == null) {
                        throw new IllegalStateException("Artist ID is null! Artist: " + artist);
                    }
                    if (museum.id() == null) {
                        throw new IllegalStateException("Museum ID is null! Museum: " + museum);
                    }

                    String paintingId = UUID.randomUUID().toString();
                    String content = DEFAULT_IMAGE_PATH;

                    PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(artist.id(), artist.name());
                    PaintingJson.MuseumInfo museumInfo = new PaintingJson.MuseumInfo(museum.id());

                    PaintingJson painting = new PaintingJson(
                            paintingId,
                            title,
                            description,
                            content,
                            null,
                            artistInfo,
                            museumInfo
                    );

                    System.out.println("Painting to create: " + painting);

                    PaintingClient client = isApiTest(context)
                            ? new PaintingApiClient()
                            : paintingClient;
                    PaintingJson created = client.createPainting(painting);
                    System.out.println("Created painting: " + created);
                    setPainting(created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getPainting().ifPresent(painting -> {
            try {
                PaintingClient client = isApiTest(context)
                        ? new PaintingApiClient()
                        : paintingClient;
                client.deletePainting(painting.id());
            } catch (Exception e) {
                System.err.println("Failed to delete painting: " + painting.id() + ", error: " + e.getMessage());
            }
        });
        context.getStore(NAMESPACE).remove(context.getUniqueId());
    }

    private boolean isApiTest(ExtensionContext context) {
        return context.getTestMethod()
                .map(method -> method.isAnnotationPresent(RestTest.class))
                .orElse(false) ||
                context.getTestClass()
                        .map(clazz -> clazz.isAnnotationPresent(RestTest.class))
                        .orElse(false);
    }

    public static void setPainting(PaintingJson painting) {
        context().getStore(NAMESPACE).put(context().getUniqueId(), painting);
    }

    public static Optional<PaintingJson> getPainting() {
        return Optional.ofNullable(context().getStore(NAMESPACE)
                .get(context().getUniqueId(), PaintingJson.class));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
    }

    @Override
    public PaintingJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getPainting().orElseThrow(() -> new IllegalStateException("Painting not found in context"));
    }
}