package guru.qa.utils;

import com.github.javafaker.Faker;

import java.util.Locale;

public class RandomDataUtils {

    private static final Faker FAKER = new Faker(new Locale("ru"));

    public static String randomUsername() {
        return FAKER.name().username() + System.currentTimeMillis();
    }

    public static String randomPassword() {
        return FAKER.internet().password(8, 12);
    }

    public static String randomArtistName() {
        return FAKER.artist().name();
    }

    public static String randomBiography() {
        return FAKER.lorem().paragraph();
    }

    public static String randomMuseumTitle() {
        return FAKER.company().name() + " Museum";
    }

    public static String randomDescription() {
        return FAKER.lorem().sentence();
    }

    public static String randomCity() {
        return FAKER.address().city();
    }

    public static String randomAddress() {
        return FAKER.address().streetAddress();
    }

    public static String randomPaintingTitle() {
        return FAKER.book().title();
    }

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    public static String randomLastName() {
        return FAKER.name().lastName();
    }
}