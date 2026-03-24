package guru.qa.rococo.controller;

import guru.qa.rococo.model.Country;
import guru.qa.rococo.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/country")
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public Page<Country> getAllCountries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Country> allCountries = countryService.getAllCountries();

        int start = Math.min(page * size, allCountries.size());
        int end = Math.min(start + size, allCountries.size());

        List<Country> pagedList = allCountries.subList(start, end);

        return new PageImpl<>(pagedList, PageRequest.of(page, size), allCountries.size());
    }

    @GetMapping("/{id}")
    public Country getCountryById(@PathVariable String id) {
        return countryService.getCountryById(id);
    }

    @GetMapping("/name/{name}")
    public Country getCountryByName(@PathVariable String name) {
        return countryService.getCountryByName(name);
    }
}