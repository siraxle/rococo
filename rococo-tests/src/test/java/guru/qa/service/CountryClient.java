package guru.qa.service;

import guru.qa.model.CountryJson;
import java.util.List;

public interface CountryClient {

    List<CountryJson> getAllCountries();

    CountryJson getCountry(String id);

    CountryJson getCountryByCode(String code);

    boolean existsById(String id);

    boolean existsByCode(String code);

    void createCountry(CountryJson country);

    void updateCountry(String id, String name, String code);

    void deleteCountry(String id);

    void deleteCountryByCode(String code);

    int getCountriesCount();
}