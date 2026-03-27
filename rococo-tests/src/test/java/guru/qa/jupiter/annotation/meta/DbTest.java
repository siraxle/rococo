package guru.qa.jupiter.annotation.meta;

import guru.qa.jupiter.extension.TestMethodContextExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TestMethodContextExtension.class)
public @interface DbTest {
}