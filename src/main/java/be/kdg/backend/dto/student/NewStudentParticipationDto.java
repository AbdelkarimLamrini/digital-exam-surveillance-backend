package be.kdg.backend.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewStudentParticipationDto {
    @NotBlank(message = "ExamId is required")
    private String examId;
    @NotBlank(message = "Classroom is required")
    private String classRoomId;
    @Pattern(regexp = "^[0-9]{7}-[0-9]{2}$", message = "StudentId must be in the format 1234567-89")
    private String studentId;
    @NotBlank(message = "FullName is required")
    private String fullName;
    @Email(message = "Email must be a valid email address")
    private String email;
}
