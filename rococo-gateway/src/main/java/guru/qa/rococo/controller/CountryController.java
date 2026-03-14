package guru.qa.rococo.controller;

import guru.qa.rococo.model.Country;  // ИЗМЕНИТЬ ИМПОРТ
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
    public List<Country> getAllCountries() {  // ИЗМЕНИТЬ ТИП
        return countryService.getAllCountries();
    }

    @GetMapping("/{id}")
    public Country getCountryById(@PathVariable String id) {  // ИЗМЕНИТЬ ТИП
        return countryService.getCountryById(id);
    }

    @GetMapping("/name/{name}")
    public Country getCountryByName(@PathVariable String name) {  // ИЗМЕНИТЬ ТИП
        return countryService.getCountryByName(name);
    }
}