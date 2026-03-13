package guru.qa.rococo.service;

import guru.qa.rococo.entity.PaintingEntity;
import guru.qa.rococo.repository.PaintingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;

    @Autowired
    public PaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    public List<PaintingEntity> getAllPaintings() {
        return paintingRepository.findAll();
    }

    public PaintingEntity getPaintingById(UUID id) {
        return paintingRepository.findById(id).orElse(null);
    }

    public PaintingEntity createPainting(PaintingEntity painting) {
        return paintingRepository.save(painting);
    }

    public PaintingEntity updatePainting(PaintingEntity painting) {
        return paintingRepository.save(painting);
    }

    public void deletePainting(UUID id) {
        paintingRepository.deleteById(id);
    }

    public List<PaintingEntity> getPaintingsByArtistId(UUID artistId) {
        return paintingRepository.findAllByArtistId(artistId, Pageable.unpaged()).getContent();
    }

    public List<PaintingEntity> getPaintingsByMuseumId(UUID museumId) {
        return paintingRepository.findAllByMuseumId(museumId, Pageable.unpaged()).getContent();
    }
}