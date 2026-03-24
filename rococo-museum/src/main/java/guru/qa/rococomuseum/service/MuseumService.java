package guru.qa.rococomuseum.service;

import guru.qa.rococomuseum.model.MuseumEntity;
import guru.qa.rococomuseum.repository.MuseumRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MuseumService {

    private final MuseumRepository museumRepository;
    private static final Logger LOG = LoggerFactory.getLogger(MuseumService.class);

    public MuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    @Transactional(readOnly = true)
    public MuseumEntity getMuseumById(UUID id) {
        return museumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Museum not found with id: " + id));
    }

    @Transactional
    public MuseumEntity createMuseum(String title, String description,
                                     String city, String address,
                                     UUID countryId, String photo) {
        if (museumRepository.existsByTitle(title)) {
            throw new RuntimeException("Museum with title '" + title + "' already exists");
        }

        LOG.info("=== Creating museum ===");
        LOG.info("Original photo length: {}", photo != null ? photo.length() : "null");

        String processedPhoto = null;
        if (photo != null && !photo.isBlank()) {
            try {
                SmallPhoto smallPhoto = new SmallPhoto(300, 300, "png", photo);
                byte[] bytes = smallPhoto.bytes();
                if (bytes != null) {
                    processedPhoto = new String(bytes, StandardCharsets.UTF_8);
                    LOG.info("Processed photo length: {}", processedPhoto.length());
                } else {
                    LOG.warn("SmallPhoto.bytes() returned null");
                    processedPhoto = photo;
                }
            } catch (Exception e) {
                LOG.error("Failed to process photo", e);
                processedPhoto = photo;
            }
        }

        LOG.info("Final photo length: {}", processedPhoto != null ? processedPhoto.length() : "null");

        MuseumEntity museum = new MuseumEntity();
        museum.setTitle(title);
        museum.setDescription(description);
        museum.setCity(city);
        museum.setAddress(address);
        museum.setCountryId(countryId);
        museum.setPhoto(processedPhoto);

        return museumRepository.save(museum);
    }

    @Transactional
    public MuseumEntity updateMuseum(UUID id, String title, String description,
                                     String city, String address,
                                     UUID countryId, String photo) {
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
        if (countryId != null) {
            museum.setCountryId(countryId);
        }
        if (photo != null) {
            String processedPhoto = null;
            if (!photo.isBlank()) {
                try {
                    SmallPhoto smallPhoto = new SmallPhoto(300, 300, "png", photo);
                    byte[] bytes = smallPhoto.bytes();
                    if (bytes != null) {
                        processedPhoto = new String(bytes, StandardCharsets.UTF_8);
                        LOG.info("Updated photo length: {}", processedPhoto.length());
                    }
                } catch (Exception e) {
                    LOG.error("Failed to process photo for update", e);
                    processedPhoto = photo;
                }
            }
            museum.setPhoto(processedPhoto);
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