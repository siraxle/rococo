package guru.qa.service;

import guru.qa.model.ArtistJson;
import java.util.List;

public interface ArtistClient {

    ArtistJson createArtist(ArtistJson artist);

    ArtistJson getArtist(String id);

    List<ArtistJson> getAllArtists();

    ArtistJson updateArtist(String id, String name, String biography, String photo);

    void deleteArtist(String id);

    boolean existsById(String id);
}