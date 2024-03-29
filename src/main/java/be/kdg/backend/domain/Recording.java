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
public class Recording {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private StudentParticipation studentParticipation;
    @CreationTimestamp
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String recordingUrl;
}
