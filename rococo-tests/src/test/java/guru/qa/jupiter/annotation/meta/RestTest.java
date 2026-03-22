package guru.qa.jupiter.annotation.meta;

import guru.qa.jupiter.extension.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
        AllureJunit5.class,
        TestMethodContextExtension.class,
        UserExtension.class,
        ApiLoginExtension.class,
        ArtistExtension.class,
        MuseumExtension.class,
        PaintingExtension.class
})
public @interface RestTest {
}