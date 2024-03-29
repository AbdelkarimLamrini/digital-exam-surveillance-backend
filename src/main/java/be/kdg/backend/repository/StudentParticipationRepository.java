package be.kdg.backend.repository;

import be.kdg.backend.domain.StudentParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentParticipationRepository extends JpaRepository<StudentParticipation, Long> {
    @Query("""
                    SELECT sp
                    FROM StudentParticipation sp
                    JOIN FETCH sp.examSession es
                    JOIN es.exam e
                    WHERE current_timestamp BETWEEN e.startTime AND e.endTime
                    AND sp.status != 3
            """)
    List<StudentParticipation> findByActiveExamAndNotTerminated();


    @Query("""
                    SELECT sp
                    FROM StudentParticipation sp
                    WHERE sp.examSession.id IN :examSessionIds
                    AND sp.status != 3
            """)
    List<StudentParticipation> findByExamSessionIdInAndNotTerminated(List<Long> examSessionIds);

    @Query("""
                    SELECT sp
                    FROM StudentParticipation sp
                    LEFT JOIN FETCH sp.recordings r
                    WHERE sp.id = :id
            """)
    Optional<StudentParticipation> findByIdWithRecordings(Long id);

    @Query("""
                    SELECT sp
                    FROM StudentParticipation sp
                    WHERE sp.examSession.id = :examSessionId
                    AND sp.studentId = :studentId
            """)
    Optional<StudentParticipation> findByExamSessionAndStudentId(Long examSessionId, String studentId);

    @Query("""
                    SELECT sp
                    FROM StudentParticipation sp
                    JOIN FETCH sp.examSession es
                    WHERE sp.id = :participationId
            """)
    Optional<StudentParticipation> findByIdWithExamSession(Long participationId);
}
