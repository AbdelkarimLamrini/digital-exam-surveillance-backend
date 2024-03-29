package be.kdg.backend.dto.recording;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordingDto {
    private Long id;
    private Long studentParticipationId;
    private String studentId;
    private String recordingUrl;
    private String startTime;
    private String endTime;
}
