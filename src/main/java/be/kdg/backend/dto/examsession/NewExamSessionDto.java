package be.kdg.backend.dto.examsession;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewExamSessionDto {
    @NotBlank(message = "Classroom is required")
    public String classRoomId;
    @NotBlank(message = "Supervisor name is required")
    public String supervisorName;
}
