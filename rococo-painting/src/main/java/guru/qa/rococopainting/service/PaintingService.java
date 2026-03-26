package guru.qa.rococopainting.service;

import guru.qa.rococopainting.model.PaintingEntity;
import guru.qa.rococopainting.repository.PaintingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaintingService {
    private static final Logger LOG = LoggerFactory.getLogger(PaintingService.class);

    private final PaintingRepository paintingRepository;

    public PaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    @Transactional(readOnly = true)
    public PaintingEntity getPaintingById(UUID id) {
        return paintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));
    }

    @Transactional
    public PaintingEntity createPainting(String title, String description,
                                         UUID artistId, UUID museumId, String photo) {
        if (paintingRepository.existsByTitleAndArtistId(title, artistId)) {
            throw new RuntimeException("Painting with title '" + title +
                    "' by artist '" + artistId + "' already exists");
        }

        LOG.info("=== Creating painting ===");
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

        PaintingEntity painting = new PaintingEntity();
        painting.setTitle(title);
        painting.setDescription(description);
        painting.setArtistId(artistId);
        painting.setMuseumId(museumId);
        painting.setPhoto(processedPhoto);

        return paintingRepository.save(painting);
    }

    @Transactional
    public PaintingEntity updatePainting(UUID id, String title, String description,
                                         UUID artistId, UUID museumId, String photo) {
        PaintingEntity painting = getPaintingById(id);

        if (title != null && !title.isBlank()) {
            painting.setTitle(title);
        }
        if (description != null) {
            painting.setDescription(description);
        }
        if (artistId != null) {
            painting.setArtistId(artistId);
        }
        if (museumId != null) {
            painting.setMuseumId(museumId);
        }
        if (photo != null) {
            String processedPhoto = null;
            if (!photo.isBlank()) {
                try {
                    SmallPhoto smallPhoto = new SmallPhoto(300, 300, "png", photo);
                    byte[] bytes = smallPhoto.bytes();
                    if (bytes != null) {
                        processedPhoto = new String(bytes, StandardCharsets.UTF_8);
                    }
                } catch (Exception e) {
                    processedPhoto = photo;
                }
            }
            painting.setPhoto(processedPhoto);
        }

        return paintingRepository.save(painting);
    }

    @Transactional
    public void deletePainting(UUID id) {
        if (!paintingRepository.existsById(id)) {
            throw new RuntimeException("Painting not found with id: " + id);
        }
        paintingRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<PaintingEntity> getAllPaintings(String title, UUID artistId,
                                                UUID museumId, Pageable pageable) {
        return paintingRepository.findByFilters(
                title != null ? title : null,
                artistId,
                museumId,
                pageable);
    }
}