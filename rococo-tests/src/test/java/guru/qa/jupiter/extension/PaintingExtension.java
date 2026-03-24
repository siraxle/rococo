package guru.qa.jupiter.extension;

import guru.qa.jupiter.annotation.Painting;
import guru.qa.model.ArtistJson;
import guru.qa.model.MuseumJson;
import guru.qa.model.PaintingJson;
import guru.qa.service.api.PaintingApiClient;
import guru.qa.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static guru.qa.jupiter.extension.TestMethodContextExtension.context;

public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);

    private final PaintingApiClient paintingApiClient = new PaintingApiClient();

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

                    PaintingJson.ArtistInfo artistInfo = new PaintingJson.ArtistInfo(artist.id(), null);  // name = null при отправке
                    PaintingJson.MuseumInfo museumInfo = new PaintingJson.MuseumInfo(museum.id());
                    PaintingJson painting = new PaintingJson(null, title, description, null, null, artistInfo, museumInfo);

                    System.out.println("Painting to create: " + painting);
                    PaintingJson created = paintingApiClient.createPainting(painting);
                    System.out.println("Created painting: " + created);
                    setPainting(created);
                });
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