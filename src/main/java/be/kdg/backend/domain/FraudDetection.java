package be.kdg.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FraudDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private StudentParticipation studentParticipation;
    @CreationTimestamp
    private LocalDateTime timestamp;
    private Double fraudScore;

    public FraudDetection(StudentParticipation studentParticipation, Double fraudScore) {
        this.studentParticipation = studentParticipation;
        this.fraudScore = fraudScore;
    }
}
