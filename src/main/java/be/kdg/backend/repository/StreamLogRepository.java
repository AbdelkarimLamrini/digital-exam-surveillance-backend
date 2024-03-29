package be.kdg.backend.repository;

import be.kdg.backend.domain.StreamLog;
import be.kdg.backend.domain.StudentParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StreamLogRepository extends JpaRepository<StreamLog, Long> {
    List<StreamLog> findByStudentParticipationOrderByTimestamp(StudentParticipation participation);
}
