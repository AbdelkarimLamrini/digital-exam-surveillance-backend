package be.kdg.backend.dto.fraud;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FraudDetectionDto {
    private Long id;
    private Long participationId;
    private String studentId;
    private LocalDateTime timestamp;
    private Double fraudScore;
}
