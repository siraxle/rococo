package guru.qa.api.core;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeCookieStore implements CookieStore {

    public static final ThreadSafeCookieStore INSTANCE = new ThreadSafeCookieStore();
    private final Map<URI, List<HttpCookie>> uriToCookies = new ConcurrentHashMap<>();

    private ThreadSafeCookieStore() {
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        uriToCookies.computeIfAbsent(uri, k -> new ArrayList<>()).add(cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return uriToCookies.getOrDefault(uri, Collections.emptyList());
    }

    @Override
    public List<HttpCookie> getCookies() {
        return uriToCookies.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public List<URI> getURIs() {
        return new ArrayList<>(uriToCookies.keySet());
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        List<HttpCookie> cookies = uriToCookies.get(uri);
        if (cookies != null) {
            return cookies.remove(cookie);
        }
        return false;
    }

    @Override
    public boolean removeAll() {
        uriToCookies.clear();
        return true;
    }

    public String cookieValue(String name) {
        return getCookies().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(HttpCookie::getValue)
                .orElse(null);
    }

    public String xsrfCookie() {
        return cookieValue("XSRF-TOKEN");
    }
}