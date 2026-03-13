package guru.qa.rococopainting.service;

import guru.qa.rococopainting.model.PaintingEntity;
import guru.qa.rococopainting.repository.PaintingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaintingService {

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

        PaintingEntity painting = new PaintingEntity();
        painting.setTitle(title);
        painting.setDescription(description);
        painting.setArtistId(artistId);
        painting.setMuseumId(museumId);
        painting.setPhoto(photo);

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
            painting.setPhoto(photo);
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