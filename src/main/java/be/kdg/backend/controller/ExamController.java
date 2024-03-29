package be.kdg.backend.controller;

import be.kdg.backend.dto.exam.ExamDetailDto;
import be.kdg.backend.dto.exam.ExamDto;
import be.kdg.backend.dto.exam.NewExamDto;
import be.kdg.backend.dto.examsession.ExamSessionDto;
import be.kdg.backend.dto.examsession.NewExamSessionDto;
import be.kdg.backend.service.ExamService;
import be.kdg.backend.service.ExamSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;
    private final ExamSessionService examSessionService;

    public ExamController(ExamService examService, ExamSessionService examSessionService) {
        this.examService = examService;
        this.examSessionService = examSessionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDetailDto> getExam(@PathVariable String id) {
        return ResponseEntity.ok(examService.getExam(id));
    }

    @GetMapping
    public ResponseEntity<List<ExamDto>> getExams() {
        return ResponseEntity.ok(examService.getExams());
    }

    @PostMapping
    public ResponseEntity<ExamDto> createExam(@Valid @RequestBody NewExamDto examDto) {
        return ResponseEntity.ok(examService.createExam(examDto));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<String> endExam(@PathVariable String id) {
        examService.endExam(id);
        return ResponseEntity.ok("Exam ended successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamDto> updateExam(@PathVariable String id, @Valid @RequestBody NewExamDto examDto) {
        return ResponseEntity.ok(examService.updateExam(id, examDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable String id) {
        examService.deleteExam(id);
        return ResponseEntity.ok("Exam deleted successfully.");
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<ExamSessionDto>> getExamSessions(@PathVariable String id) {
        return ResponseEntity.ok(examSessionService.getExamSessions(id));
    }

    @PostMapping("/{id}/sessions")
    public ResponseEntity<ExamSessionDto> createExamSession(@PathVariable String id, @RequestBody NewExamSessionDto examSessionDto) {
        return ResponseEntity.ok(examSessionService.createExamSession(id, examSessionDto));
    }
}
