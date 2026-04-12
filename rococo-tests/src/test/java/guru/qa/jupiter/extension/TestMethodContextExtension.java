package guru.qa.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Расширение JUnit 5 для управления контекстом тестовых методов.
 * Обеспечивает доступ к текущему {@link ExtensionContext} через ThreadLocal.
 */
@ParametersAreNonnullByDefault
public class TestMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ThreadLocal<ExtensionContext> ctxStore = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        ctxStore.set(context);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        ctxStore.remove();
    }

    public static ExtensionContext context() {
        return ctxStore.get();
    }
}