package guru.qa.rococomuseum.service;

import guru.qa.rococomuseum.model.MuseumEntity;
import guru.qa.rococomuseum.repository.MuseumRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MuseumService {

    private final MuseumRepository museumRepository;

    public MuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    @Transactional(readOnly = true)
    public MuseumEntity getMuseumById(UUID id) {
        return museumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Museum not found with id: " + id));
    }

    @Transactional
    public MuseumEntity createMuseum(String title, String description, String city,
                                     String address, String photo, UUID countryId) {
        if (museumRepository.existsByTitleAndCity(title, city)) {
            throw new RuntimeException("Museum with title '" + title + "' in city '" + city + "' already exists");
        }

        MuseumEntity museum = new MuseumEntity();
        museum.setTitle(title);
        museum.setDescription(description);
        museum.setCity(city);
        museum.setAddress(address);
        museum.setPhoto(photo);
        museum.setCountryId(countryId);

        return museumRepository.save(museum);
    }

    @Transactional
    public MuseumEntity updateMuseum(UUID id, String title, String description, String city,
                                     String address, String photo, UUID countryId) {
        MuseumEntity museum = getMuseumById(id);

        if (title != null && !title.isBlank()) {
            museum.setTitle(title);
        }
        if (description != null) {
            museum.setDescription(description);
        }
        if (city != null && !city.isBlank()) {
            museum.setCity(city);
        }
        if (address != null && !address.isBlank()) {
            museum.setAddress(address);
        }
        if (photo != null) {
            museum.setPhoto(photo);
        }
        if (countryId != null) {
            museum.setCountryId(countryId);
        }

        return museumRepository.save(museum);
    }

    @Transactional
    public void deleteMuseum(UUID id) {
        if (!museumRepository.existsById(id)) {
            throw new RuntimeException("Museum not found with id: " + id);
        }
        museumRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<MuseumEntity> getAllMuseums(String title, String city, Pageable pageable) {
        if (title != null || city != null) {
            return museumRepository.findByTitleAndCityContainingIgnoreCase(
                    title != null ? title : "",
                    city != null ? city : "",
                    pageable);
        }
        return museumRepository.findAll(pageable);
    }
}