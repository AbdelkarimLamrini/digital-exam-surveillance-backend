package be.kdg.backend.repository;

import be.kdg.backend.domain.Recording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecordingRepository extends JpaRepository<Recording, Long> {
    List<Recording> findByStudentParticipationIdAndEndTimeIsNull(Long participationId);

    @Query("""
            SELECT r
            FROM Recording r
            LEFT JOIN FETCH r.studentParticipation
            WHERE r.id = :id
            """)
    Optional<Recording> findByIdWithParticipation(Long id);

    Optional<Recording> findLastByStudentParticipationId(Long participationId);
}
