package be.kdg.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(name = "timestamp_index", columnList = "timestamp")})
public class StreamLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private StudentParticipation studentParticipation;
    private LocalDateTime timestamp;
    private ConnectionStatus status;

    public StreamLog(StudentParticipation studentParticipation, LocalDateTime timestamp) {
        this.studentParticipation = studentParticipation;
        this.timestamp = timestamp;
    }

    public StreamLog(StudentParticipation studentParticipation, LocalDateTime timestamp, ConnectionStatus status) {
        this.studentParticipation = studentParticipation;
        this.timestamp = timestamp;
        this.status = status;
    }
}
