package be.kdg.backend.dto.exam;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamDto {
    private String id;
    private String name;
    private String creatorName;
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
