package be.kdg.backend.dto.student;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class StudentParticipationFlutterDto {
    private Long id;
    private String examId;
    private String classRoomId;
    private String studentId;
    private String fullName;
    private String email;
    private LocalDateTime startTime;
    private String rtmpStreamUrl;
}
