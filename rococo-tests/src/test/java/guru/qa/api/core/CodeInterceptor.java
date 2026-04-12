package guru.qa.api.core;

import guru.qa.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeInterceptor implements Interceptor {

    private static final Pattern CODE_PATTERN = Pattern.compile("code=([^&]+)");

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.isRedirect()) {
            String location = response.header("Location");
            if (location != null && location.contains("code=")) {
                Matcher matcher = CODE_PATTERN.matcher(location);
                if (matcher.find()) {
                    String code = matcher.group(1);
                    ApiLoginExtension.setCode(code);
                }
            }
        }
        return response;
    }
}