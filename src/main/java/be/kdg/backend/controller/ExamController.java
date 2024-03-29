package be.kdg.backend.controller;

import be.kdg.backend.dto.exam.ExamDetailDto;
import be.kdg.backend.dto.exam.ExamDto;
import be.kdg.backend.dto.exam.NewExamDto;
import be.kdg.backend.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
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
}
