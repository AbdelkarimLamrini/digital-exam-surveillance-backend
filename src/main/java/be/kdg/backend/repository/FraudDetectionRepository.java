package be.kdg.backend.repository;

import be.kdg.backend.domain.FraudDetection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudDetectionRepository extends JpaRepository<FraudDetection, Long> {
    List<FraudDetection> findByStudentParticipationId(Long studentParticipationId);
}
