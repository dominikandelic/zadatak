package hr.dandelic.zadatak.persistence.repository;

import hr.dandelic.zadatak.persistence.model.CardApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardApplicationRepository extends JpaRepository<CardApplication, Long> {
    List<CardApplication> findAllByOib(long oib);
    Optional<CardApplication> findByOibAndStatusIsIn(long oib, List<CardApplication.CardApplicationStatus> statuses);
}
