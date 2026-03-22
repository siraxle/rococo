package guru.qa.rococo.service;

import guru.qa.rococo.grpc.ArtistGrpcClient;
import guru.qa.rococo.model.Artist;
import org.springframework.stereotype.Service;
import rococo.grpc.artist.ArtistIdRequest;
import rococo.grpc.artist.ArtistResponse;
import rococo.grpc.artist.ArtistListResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    private final ArtistGrpcClient artistGrpcClient;

    public ArtistService(ArtistGrpcClient artistGrpcClient) {
        this.artistGrpcClient = artistGrpcClient;
    }

    public Artist getArtistById(UUID id) {
        ArtistResponse response = artistGrpcClient.getArtist(id.toString());
        return mapToArtist(response);
    }

    public Artist createArtist(String name, String biography, String photo) {
        ArtistResponse response = artistGrpcClient.createArtist(name, biography, photo);
        return mapToArtist(response);
    }

    public Artist updateArtist(UUID id, String name, String biography, String photo) {
        ArtistResponse response = artistGrpcClient.updateArtist(id.toString(), name, biography, photo);
        return mapToArtist(response);
    }

    public List<Artist> getAllArtists() {
        return getAllArtists(0, 10);
    }

    public List<Artist> getAllArtists(int page, int size) {
        ArtistListResponse response = artistGrpcClient.getAllArtists(page, size);
        return response.getArtistsList().stream()
                .map(this::mapToArtist)
                .collect(Collectors.toList());
    }

    public void deleteArtist(String id) {
        artistGrpcClient.deleteArtist(id);
    }

    private Artist mapToArtist(ArtistResponse response) {
        return new Artist(
                UUID.fromString(response.getId()),
                response.getName(),
                response.getBiography(),
                response.getPhoto()
        );
    }

    public String getArtistName(UUID id) {
        try {
            Artist artist = getArtistById(id);
            return artist != null ? artist.name() : null;
        } catch (Exception e) {
            return null;
        }
    }

}