package be.kdg.backend.controller;

import be.kdg.backend.dto.fraud.FraudDetectionDto;
import be.kdg.backend.dto.recording.RecordingDto;
import be.kdg.backend.dto.streamlog.StreamLogDto;
import be.kdg.backend.dto.student.NewStudentParticipationDto;
import be.kdg.backend.dto.student.StudentParticipationAIDto;
import be.kdg.backend.dto.student.StudentParticipationDto;
import be.kdg.backend.dto.student.StudentParticipationFlutterDto;
import be.kdg.backend.service.FraudDetectionService;
import be.kdg.backend.service.StudentParticipationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-participations")
public class StudentParticipationController {
    private final StudentParticipationService studentParticipationService;
    private final FraudDetectionService fraudDetectionService;

    public StudentParticipationController(StudentParticipationService studentParticipationService, FraudDetectionService fraudDetectionService) {
        this.studentParticipationService = studentParticipationService;
        this.fraudDetectionService = fraudDetectionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentParticipationDto> getStudentParticipation(@PathVariable Long id) {
        return ResponseEntity.ok(studentParticipationService.getStudentParticipation(id));
    }

    @PostMapping
    public ResponseEntity<StudentParticipationFlutterDto> registerStudentParticipation(@Valid @RequestBody NewStudentParticipationDto participationDto) {
        return ResponseEntity.ok(studentParticipationService.registerStudentParticipation(participationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> terminateStudentParticipation(@PathVariable Long id) {
        studentParticipationService.endStudentParticipation(id);
        return ResponseEntity.ok("Student participation terminated");
    }

    @GetMapping("/active")
    public ResponseEntity<List<StudentParticipationAIDto>> getStudentParticipations() {
        return ResponseEntity.ok(studentParticipationService.getActiveStudentParticipations());
    }

    @GetMapping("/{id}/recordings")
    public ResponseEntity<List<RecordingDto>> getRecordings(@PathVariable Long id) {
        return ResponseEntity.ok(studentParticipationService.getRecordings(id));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<StreamLogDto>> getLogs(@PathVariable Long id) {
        return ResponseEntity.ok(studentParticipationService.getLogsForStudentParticipation(id));
    }

    @GetMapping("/{id}/fraud-detections")
    public ResponseEntity<List<FraudDetectionDto>> getFraudDetections(@PathVariable Long id) {
        return ResponseEntity.ok(fraudDetectionService.getFraudDetectionsForParticipation(id));
    }
}
