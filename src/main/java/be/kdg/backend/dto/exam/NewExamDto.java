package be.kdg.backend.dto.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewExamDto {
    @NotBlank(message = "Id is required")
    private String id;
    @NotBlank(message = "Name is required")
    private String name;
    private String creatorName;
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
}
