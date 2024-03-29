package be.kdg.backend.dto.student;

import be.kdg.backend.domain.ConnectionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class StudentParticipationDto {
    private Long id;
    private String examId;
    private String classRoomId;
    private String studentId;
    private String fullName;
    private String email;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String hlsStreamUrl;
    private ConnectionStatus status;
    private boolean isRecording;
}
