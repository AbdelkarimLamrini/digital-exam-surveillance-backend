package be.kdg.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(name = "exam_id_index", columnList = "id")}, uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;
    private String id;
    private String name;
    private String creatorName;
    @OneToMany(mappedBy = "exam")
    private List<ExamSession> examSessions;
    @CreationTimestamp
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public long getDurationHours() {
        return startTime.until(endTime, ChronoUnit.HOURS);
    }
}
