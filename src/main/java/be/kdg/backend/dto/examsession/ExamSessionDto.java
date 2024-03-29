package be.kdg.backend.dto.examsession;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamSessionDto {
    private Long id;
    private String classRoomId;
    private String supervisorName;
}
