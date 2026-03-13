package guru.qa.rococo.service;

import guru.qa.rococo.entity.CountryEntity;
import guru.qa.rococo.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryEntity> getAllCountries() {
        return countryRepository.findAll();
    }

    public CountryEntity getCountryById(UUID id) {
        return countryRepository.findById(id).orElse(null);
    }

    public CountryEntity getCountryByName(String name) {
        return countryRepository.findByName(name).orElse(null);
    }

    public CountryEntity getCountryByCity(String city) {
        return switch (city.toLowerCase()) {
            case "париж" -> countryRepository.findByName("Франция").orElse(null);
            case "санкт-петербург", "москва" -> countryRepository.findByName("Россия").orElse(null);
            case "флоренция", "рим" -> countryRepository.findByName("Италия").orElse(null);
            case "берлин" -> countryRepository.findByName("Германия").orElse(null);
            case "мадрид" -> countryRepository.findByName("Испания").orElse(null);
            default -> null;
        };
    }
}