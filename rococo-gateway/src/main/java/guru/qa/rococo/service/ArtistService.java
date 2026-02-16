package guru.qa.rococo.service;

import guru.qa.rococo.entity.ArtistEntity;
import guru.qa.rococo.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistEntity> getAllArtists() {
        return artistRepository.findAll();
    }

    public ArtistEntity getArtistById(UUID id) {
        return artistRepository.findById(id).orElse(null);
    }

    public ArtistEntity createArtist(ArtistEntity artist) {
        return artistRepository.save(artist);
    }

    public ArtistEntity updateArtist(ArtistEntity artist) {
        return artistRepository.save(artist);
    }

    public void deleteArtist(UUID id) {
        artistRepository.deleteById(id);
    }
}