package guru.qa.rococoartist.service;

import guru.qa.rococoartist.model.ArtistEntity;
import guru.qa.rococoartist.repository.ArtistRepository;
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

    public ArtistEntity getArtistById(UUID id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found with id: " + id));
    }

    public ArtistEntity createArtist(String name, String biography, String photo) {
        ArtistEntity artist = new ArtistEntity();
        artist.setName(name);
        artist.setBiography(biography);
        artist.setPhoto(photo);

        return artistRepository.save(artist);
    }

    public ArtistEntity updateArtist(UUID id, String name, String biography, String photo) {
        ArtistEntity artist = getArtistById(id);

        if (name != null && !name.isBlank()) {
            artist.setName(name);
        }
        if (biography != null) {
            artist.setBiography(biography);
        }
        if (photo != null) {
            artist.setPhoto(photo);
        }

        return artistRepository.save(artist);
    }

    public List<ArtistEntity> getAllArtists() {
        return artistRepository.findAll();
    }

    public void deleteArtist(UUID id) {
        if (!artistRepository.existsById(id)) {
            throw new RuntimeException("Artist not found with id: " + id);
        }
        artistRepository.deleteById(id);
    }
}