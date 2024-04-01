package be.kdg.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Exam {
    @Id
    private String id;
    private String name;
    private String creatorName;
    @OneToMany(mappedBy = "exam")
    private List<ExamSession> examSessions;
    @CreationTimestamp
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
