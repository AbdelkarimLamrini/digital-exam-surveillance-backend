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
@RequestMapping("/api/exam-sessions")
public class ExamSessionController {
    private final ExamSessionService examSessionService;

    public ExamSessionController(ExamSessionService examSessionService) {
        this.examSessionService = examSessionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamSessionDetailDto> getExamSession(@PathVariable Long id) {
        return ResponseEntity.ok(examSessionService.getExamSession(id));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<String> endExamSession(@PathVariable Long id) {
        examSessionService.endExamSession(id);
        return ResponseEntity.ok("Exam session ended successfully.");
    }

    @GetMapping("/{id}/participations")
    public ResponseEntity<List<StudentParticipationDto>> getStudentParticipations(@PathVariable Long id) {
        return ResponseEntity.ok(examSessionService.getStudentParticipations(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamSessionDto> updateExamSession(@PathVariable Long id, @RequestBody NewExamSessionDto examSessionDto) {
        return ResponseEntity.ok(examSessionService.updateExamSession(id, examSessionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExamSession(@PathVariable Long id) {
        examSessionService.deleteExamSession(id);
        return ResponseEntity.ok("Exam session deleted successfully.");
    }
}
