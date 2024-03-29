package be.kdg.backend.controller;

import be.kdg.backend.dto.examsession.ExamSessionDetailDto;
import be.kdg.backend.dto.examsession.ExamSessionDto;
import be.kdg.backend.dto.examsession.NewExamSessionDto;
import be.kdg.backend.dto.student.StudentParticipationDto;
import be.kdg.backend.service.ExamSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam/{examId}/session")
public class ExamSessionController {
    private final ExamSessionService examSessionService;

    public ExamSessionController(ExamSessionService examSessionService) {
        this.examSessionService = examSessionService;
    }

    @GetMapping
    public ResponseEntity<List<ExamSessionDto>> getExamSessions(@PathVariable String examId) {
        return ResponseEntity.ok(examSessionService.getExamSessions(examId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSessionDetailDto> getExamSession(@PathVariable String examId, @PathVariable Long id) {
        return ResponseEntity.ok(examSessionService.getExamSession(id));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<String> endExamSession(@PathVariable String examId, @PathVariable Long id) {
        examSessionService.endExamSession(id);
        return ResponseEntity.ok("Exam session ended successfully.");
    }

    @GetMapping("/{id}/participation")
    public ResponseEntity<List<StudentParticipationDto>> getStudentParticipations(@PathVariable String examId, @PathVariable Long id) {
        return ResponseEntity.ok(examSessionService.getStudentParticipations(id));
    }

    @PostMapping
    public ResponseEntity<ExamSessionDto> createExamSession(@PathVariable String examId, @RequestBody NewExamSessionDto examSessionDto) {
        return ResponseEntity.ok(examSessionService.createExamSession(examId, examSessionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamSessionDto> updateExamSession(@PathVariable String examId, @PathVariable Long id, @RequestBody NewExamSessionDto examSessionDto) {
        return ResponseEntity.ok(examSessionService.updateExamSession(examId, id, examSessionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExamSession(@PathVariable String examId, @PathVariable Long id) {
        examSessionService.deleteExamSession(id);
        return ResponseEntity.ok("Exam session deleted successfully.");
    }
}
