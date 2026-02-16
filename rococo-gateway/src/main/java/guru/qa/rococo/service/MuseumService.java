package guru.qa.rococo.service;

import guru.qa.rococo.entity.MuseumEntity;
import guru.qa.rococo.repository.MuseumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MuseumService {

    private final MuseumRepository museumRepository;

    @Autowired
    public MuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    public List<MuseumEntity> getAllMuseums() {
        return museumRepository.findAll();
    }

    public MuseumEntity getMuseumById(UUID id) {
        return museumRepository.findById(id).orElse(null);
    }

    public MuseumEntity createMuseum(MuseumEntity museum) {
        return museumRepository.save(museum);
    }

    public MuseumEntity updateMuseum(MuseumEntity museum) {
        return museumRepository.save(museum);
    }

    public void deleteMuseum(UUID id) {
        museumRepository.deleteById(id);
    }
}