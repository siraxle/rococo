package guru.qa.service;

import guru.qa.model.MuseumJson;
import java.util.List;

public interface MuseumClient {

    MuseumJson createMuseum(MuseumJson museum);

    MuseumJson getMuseum(String id);

    List<MuseumJson> getAllMuseums();

    MuseumJson updateMuseum(String id, String title, String description, String city, String address, String photo);

    void deleteMuseum(String id);

    boolean existsById(String id);
}