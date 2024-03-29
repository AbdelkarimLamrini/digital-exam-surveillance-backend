package be.kdg.backend.dto.exam;

import be.kdg.backend.dto.examsession.ExamSessionDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExamDetailDto {
    private String id;
    private String name;
    private String creatorName;
    private List<ExamSessionDto> examSessions;
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
