package guru.qa.rococopainting.repository;

import guru.qa.rococopainting.model.PaintingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {

    @Query("SELECT p FROM PaintingEntity p WHERE " +
            "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:artistId IS NULL OR p.artistId = :artistId) AND " +
            "(:museumId IS NULL OR p.museumId = :museumId)")
    Page<PaintingEntity> findByFilters(
            @Param("title") String title,
            @Param("artistId") UUID artistId,
            @Param("museumId") UUID museumId,
            Pageable pageable);

    boolean existsByTitleAndArtistId(String title, UUID artistId);
}