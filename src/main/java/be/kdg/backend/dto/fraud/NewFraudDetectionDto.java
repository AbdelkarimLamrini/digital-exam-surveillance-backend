package be.kdg.backend.dto.fraud;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewFraudDetectionDto {
    @Min(value = 1, message = "ParticipationId must be greater than 0")
    @NotNull(message = "ParticipationId is required")
    private Long participationId;
    @NotBlank(message = "StudentId is required")
    private String studentId;
    @Min(value = 0, message = "FraudScore must be between 0 and 1")
    @Max(value = 1, message = "FraudScore must be between 0 and 1")
    @NotNull
    private Double fraudScore;
}
