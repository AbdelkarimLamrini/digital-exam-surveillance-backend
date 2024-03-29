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
    @Min(1)
    @NotNull
    private Long participationId;
    @NotBlank
    private String studentId;
    @Min(0)
    @Max(1)
    @NotNull
    private Double fraudScore;
}
