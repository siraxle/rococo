package guru.qa.rococogeo.repository;

import guru.qa.rococogeo.model.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
    Optional<CountryEntity> findByCode(String code);
    Optional<CountryEntity> findByName(String name);
}