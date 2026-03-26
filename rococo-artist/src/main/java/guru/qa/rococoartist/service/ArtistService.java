package guru.qa.rococoartist.service;

import guru.qa.rococoartist.model.ArtistEntity;
import guru.qa.rococoartist.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import rococo.grpc.artist.ArtistListResponse;
import rococo.grpc.artist.ArtistResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private static final Logger LOG = LoggerFactory.getLogger(ArtistService.class);

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public ArtistEntity getArtistById(UUID id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found with id: " + id));
    }

    public ArtistEntity createArtist(String name, String biography, String photo) {
        LOG.info("=== Creating artist ===");
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

        ArtistEntity artist = new ArtistEntity();
        artist.setName(name);
        artist.setBiography(biography);
        artist.setPhoto(processedPhoto);

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
            artist.setPhoto(processedPhoto);
        }

        return artistRepository.save(artist);
    }

    public ArtistListResponse getAllArtists(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ArtistEntity> artistPage = artistRepository.findAll(pageable);

        List<ArtistResponse> artistResponses = artistPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ArtistListResponse.newBuilder()
                .addAllArtists(artistResponses)
                .setTotalPages(artistPage.getTotalPages())
                .setTotalElements((int) artistPage.getTotalElements())
                .setCurrentPage(artistPage.getNumber())
                .build();
    }

    private ArtistResponse mapToResponse(ArtistEntity entity) {
        return ArtistResponse.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .setBiography(entity.getBiography() != null ? entity.getBiography() : "")
                .setPhoto(entity.getPhoto() != null ? entity.getPhoto() : "")
                .build();
    }

    public void deleteArtist(UUID id) {
        if (!artistRepository.existsById(id)) {
            throw new RuntimeException("Artist not found with id: " + id);
        }
        artistRepository.deleteById(id);
    }
}