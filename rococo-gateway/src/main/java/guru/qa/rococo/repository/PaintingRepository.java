package guru.qa.rococo.repository;

import guru.qa.rococo.entity.PaintingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
    Page<PaintingEntity> findAllByTitleContainsIgnoreCase(String title, Pageable pageable);

    Page<PaintingEntity> findAllByArtistId(UUID artistId, Pageable pageable);

    Page<PaintingEntity> findAllByMuseumId(UUID museumId, Pageable pageable);
}