package be.kdg.backend.dto.examsession;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewExamSessionDto {
    @NotBlank
    public String classRoomId;
    @NotBlank
    public String supervisorName;
}
