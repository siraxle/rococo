package guru.qa.service;

import guru.qa.model.PaintingJson;
import java.util.List;

public interface PaintingClient {

    PaintingJson createPainting(PaintingJson painting);

    PaintingJson getPainting(String id);

    List<PaintingJson> getAllPaintings();

    PaintingJson updatePainting(String id, String title, String description, String photo);

    void deletePainting(String id);

    boolean existsById(String id);
}