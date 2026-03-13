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
}