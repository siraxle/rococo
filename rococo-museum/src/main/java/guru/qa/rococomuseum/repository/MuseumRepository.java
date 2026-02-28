package guru.qa.rococomuseum.repository;

import guru.qa.rococomuseum.model.MuseumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {

    @Query("SELECT m FROM MuseumEntity m WHERE " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:city IS NULL OR LOWER(m.city) LIKE LOWER(CONCAT('%', :city, '%')))")
    Page<MuseumEntity> findByTitleAndCityContainingIgnoreCase(
            @Param("title") String title,
            @Param("city") String city,
            Pageable pageable);

    boolean existsByTitleAndCity(String title, String city);
}