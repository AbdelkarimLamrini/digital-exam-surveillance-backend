package be.kdg.backend.repository;

import be.kdg.backend.domain.Exam;
import be.kdg.backend.domain.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    List<ExamSession> findByExam(Exam exam);

    Optional<ExamSession> findByExamIdAndClassRoomId(String examId, String classRoomId);

    @Query("""
            SELECT s FROM ExamSession s
            LEFT JOIN FETCH s.participations
            JOIN FETCH s.exam
            WHERE s.id = :sessionId
            """)
    Optional<ExamSession> findByIdWithParticipations(Long sessionId);

    @Query("""
            SELECT s FROM ExamSession s
            LEFT JOIN s.exam e
            WHERE current_timestamp BETWEEN e.startTime AND e.endTime
            """)
    List<ExamSession> findActiveSessions();
}
