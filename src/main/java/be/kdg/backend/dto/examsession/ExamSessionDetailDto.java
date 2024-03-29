package be.kdg.backend.dto.examsession;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamSessionDetailDto {
    private Long id;
    private String examId;
    private String examName;
    private String classRoomId;
    private String supervisorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
