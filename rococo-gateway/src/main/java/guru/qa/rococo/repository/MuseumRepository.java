package guru.qa.rococo.repository;

import guru.qa.rococo.entity.MuseumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {
    Page<MuseumEntity> findAllByTitleContainsIgnoreCase(String title, Pageable pageable);
}