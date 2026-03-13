package guru.qa.rococo.controller;

import guru.qa.rococo.entity.CountryEntity;
import guru.qa.rococo.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/country")
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public List<CountryEntity> getAllCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("/{id}")
    public CountryEntity getCountryById(@PathVariable UUID id) {
        return countryService.getCountryById(id);
    }

    @GetMapping("/name/{name}")
    public CountryEntity getCountryByName(@PathVariable String name) {
        return countryService.getCountryByName(name);
    }
}