package be.kdg.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class StudentParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ExamSession examSession;
    @OneToMany(mappedBy = "studentParticipation")
    private List<Recording> recordings;
    private String studentId;
    private String fullName;
    private String email;
    @CreationTimestamp
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String rtmpStreamUrl;
    private String hlsStreamUrl;
    private ConnectionStatus status;
    private boolean isRecording;
}
