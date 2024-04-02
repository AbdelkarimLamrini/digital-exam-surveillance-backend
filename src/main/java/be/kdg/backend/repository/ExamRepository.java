package be.kdg.backend.repository;

import be.kdg.backend.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, String> {
    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.examSessions WHERE e.id = :id")
    Optional<Exam> findByExamIdWithSessions(String id);

    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.examSessions WHERE e.id = :id")
    Optional<Exam> findByExamId(String id);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Exam e WHERE e.id = :id")
    boolean existsByExamId(String id);
}
