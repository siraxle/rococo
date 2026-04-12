package guru.qa.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextHolder {

    private static volatile ApplicationContext context;

    public static ApplicationContext getContext() {
        if (context == null) {
            synchronized (ApplicationContextHolder.class) {
                if (context == null) {
                    context = new AnnotationConfigApplicationContext(DatabaseConfig.class);
                }
            }
        }
        return context;
    }

    public static void closeContext() {
        if (context != null) {
            synchronized (ApplicationContextHolder.class) {
                if (context != null && context instanceof AnnotationConfigApplicationContext) {
                    ((AnnotationConfigApplicationContext) context).close();
                    context = null;
                }
            }
        }
    }
}