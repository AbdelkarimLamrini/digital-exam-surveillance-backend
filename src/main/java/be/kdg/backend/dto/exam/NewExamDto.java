package be.kdg.backend.dto.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewExamDto {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
    private String creatorName;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
}
