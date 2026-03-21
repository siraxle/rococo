package guru.qa.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Интерфейс для расширений, которые должны выполняться один раз за всю тестовую сессию.
 * Предоставляет методы beforeSuite и afterSuite, аналогичные @BeforeAll/@AfterAll,
 * но гарантирует выполнение только один раз для всех тестовых классов.
 */
public interface SuiteExtension extends BeforeAllCallback {

    @Override
    default void beforeAll(ExtensionContext context) throws Exception {
        final ExtensionContext rootContext = context.getRoot();
        rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(
                        this.getClass(),
                        key -> {
                            beforeSuite(rootContext);
                            return (ExtensionContext.Store.CloseableResource) this::afterSuite;
                        }
                );
    }

    default void beforeSuite(ExtensionContext context) {
    }

    default void afterSuite() {
    }
}