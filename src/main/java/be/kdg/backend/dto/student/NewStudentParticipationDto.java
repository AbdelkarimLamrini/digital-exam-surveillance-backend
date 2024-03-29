package be.kdg.backend.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewStudentParticipationDto {
    @NotBlank
    private String examId;
    @NotBlank
    private String classRoomId;
    @Pattern(regexp = "^[0-9]{7}-[0-9]{2}$")
    private String studentId;
    @NotBlank
    private String fullName;
    @Email
    private String email;
}
