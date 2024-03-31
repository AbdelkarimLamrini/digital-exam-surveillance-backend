package be.kdg.backend.service;

import be.kdg.backend.dto.exam.ExamDetailDto;
import be.kdg.backend.dto.exam.ExamDto;
import be.kdg.backend.dto.exam.NewExamDto;
import be.kdg.backend.exception.NotUniqueException;
import be.kdg.backend.exception.ResourceNotFoundException;
import be.kdg.backend.mapper.ExamMapper;
import be.kdg.backend.repository.ExamRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final ExamSessionService examSessionService;

    public ExamService(ExamRepository examRepository, ExamMapper examMapper, ExamSessionService examSessionService) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
        this.examSessionService = examSessionService;
    }

    @Transactional
    public ExamDetailDto getExam(String id) {
        var tmp = examRepository.findByIdWithSessions(id);
        if (tmp.isEmpty()) {
            log.info("Tried to GET non-existing Exam [%s]".formatted(id));
            throw new ResourceNotFoundException("Exam with id %s not found".formatted(id));
        }
        var exam = tmp.get();
        return examMapper.toDetailDto(exam);
    }

    @Transactional
    public List<ExamDto> getExams() {
        var exams = examRepository.findAll();
        return examMapper.toDto(exams);
    }

    @Transactional
    public ExamDto createExam(NewExamDto examDto) {
        if (examRepository.existsById(examDto.getId())) {
            log.info("Tried to CREATE Exam with id [%s] that already exists".formatted(examDto.getId()));
            throw new NotUniqueException("Exam with id %s already exists".formatted(examDto.getId()));
        }
        var exam = examMapper.toDomain(examDto);
        if (exam.getStartTime().isAfter(exam.getEndTime())) {
            log.info("Tried to CREATE Exam with start time [%s] after end time [%s]".formatted(exam.getStartTime(), exam.getEndTime()));
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (exam.getStartTime().until(exam.getEndTime(), ChronoUnit.HOURS) > 6) {
            log.info("Tried to CREATE Exam with duration longer than 6 hours");
            throw new IllegalArgumentException("Exam duration must be less than 6 hours");
        }
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Transactional
    public ExamDto updateExam(String id, NewExamDto examDto) {
        var tmp = examRepository.findById(id);
        if (tmp.isEmpty()) {
            log.info("Tried to UPDATE non-existing Exam [%s]".formatted(id));
            throw new ResourceNotFoundException("Exam with id %s not found".formatted(id));
        }
        var creationTime = tmp.get().getCreationTime();
        var exam = examMapper.toDomain(examDto);
        exam.setCreationTime(creationTime);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Transactional
    public void deleteExam(String id) {
        if (!examRepository.existsById(id)) {
            log.info("Tried to DELETE non-existing Exam [%s]".formatted(id));
            throw new ResourceNotFoundException("Exam with id %s not found".formatted(id));
        }
        examRepository.deleteById(id);
    }

    @Transactional
    public void endExam(String id) {
        var tmp = examRepository.findByIdWithSessions(id);
        if (tmp.isEmpty()) {
            log.info("Tried to END non-existing Exam [%s]".formatted(id));
            throw new ResourceNotFoundException("Exam with id %s not found".formatted(id));
        }
        var exam = tmp.get();
        var sessions = exam.getExamSessions();
        for (var session: sessions){
            // TODO: optimize this process
            examSessionService.endExamSession(session.getId());
        }
    }
}
