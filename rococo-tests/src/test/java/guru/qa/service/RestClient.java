package guru.qa.service;

import guru.qa.api.core.ThreadSafeCookieStore;
import guru.qa.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookiePolicy;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();

    private final OkHttpClient client;
    private final Retrofit retrofit;

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, (Interceptor[]) null);
    }

    public RestClient(String baseUrl, HttpLoggingInterceptor.Level level) {
        this(baseUrl, false, JacksonConverterFactory.create(), level, (Interceptor[]) null);
    }

    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, (Interceptor[]) null);
    }

    public RestClient(String baseUrl, boolean followRedirect, Interceptor... interceptors) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, interceptors);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory) {
        this(baseUrl, followRedirect, converterFactory, HttpLoggingInterceptor.Level.HEADERS, (Interceptor[]) null);
    }

    public RestClient(String baseUrl, Converter.Factory converterFactory) {
        this(baseUrl, false, converterFactory, HttpLoggingInterceptor.Level.HEADERS, (Interceptor[]) null);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, Interceptor... interceptors) {
        this(baseUrl, followRedirect, converterFactory, HttpLoggingInterceptor.Level.HEADERS, interceptors);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory,
                      HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);

        if (isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                clientBuilder.addNetworkInterceptor(interceptor);
            }
        }

        clientBuilder
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level))
                .addNetworkInterceptor(new AllureOkHttp3())
                .cookieJar(new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                ));

        this.client = clientBuilder.build();
        this.retrofit = new Retrofit.Builder()
                .client(this.client)
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .build();
    }

    @Nonnull
    public <T> T create(Class<T> service) {
        return this.retrofit.create(service);
    }

    @ParametersAreNonnullByDefault
    public static final class EmptyRestClient extends RestClient {
        public EmptyRestClient(String baseUrl) {
            super(baseUrl);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirect) {
            super(baseUrl, followRedirect);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirect, Interceptor... interceptors) {
            super(baseUrl, followRedirect, interceptors);
        }

        public EmptyRestClient(String baseUrl, Converter.Factory factory) {
            super(baseUrl, factory);
        }

        public EmptyRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory,
                               HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
            super(baseUrl, followRedirect, factory, level, interceptors);
        }
    }
}