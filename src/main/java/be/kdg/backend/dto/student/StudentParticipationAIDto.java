package be.kdg.backend.dto.student;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentParticipationAIDto {
    private Long id;
    private String studentId;
    private String hlsStreamUrl;
}
