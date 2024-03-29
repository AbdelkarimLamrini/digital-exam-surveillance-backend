package be.kdg.backend.controller;

import be.kdg.backend.dto.recording.RecordingDto;
import be.kdg.backend.service.RecordingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recordings")
public class RecordingController {
    private final RecordingService recordingService;

    public RecordingController(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @PostMapping
    public ResponseEntity<RecordingDto> createRecording(@RequestParam Long participationId) {
        return ResponseEntity.ok(recordingService.createRecording(participationId));
    }

    @PostMapping("/stop")
    public ResponseEntity<RecordingDto> stopRecordingForStudentParticipation(@RequestParam Long participationId) {
        var recordingDto = recordingService.endRecordingForParticipation(participationId);
        return ResponseEntity.ok(recordingDto);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<String> stopRecording(@PathVariable Long id) {
        recordingService.endRecording(id);
        return ResponseEntity.ok("Recording stopped");
    }
}
