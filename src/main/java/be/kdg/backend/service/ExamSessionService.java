package be.kdg.backend.service;

import be.kdg.backend.dto.examsession.ExamSessionDetailDto;
import be.kdg.backend.dto.examsession.ExamSessionDto;
import be.kdg.backend.dto.examsession.NewExamSessionDto;
import be.kdg.backend.dto.student.StudentParticipationDto;
import be.kdg.backend.exception.NotUniqueException;
import be.kdg.backend.exception.ResourceNotFoundException;
import be.kdg.backend.mapper.ExamSessionMapper;
import be.kdg.backend.mapper.StudentParticipationMapper;
import be.kdg.backend.repository.ExamRepository;
import be.kdg.backend.repository.ExamSessionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExamSessionService {
    private final ExamRepository examRepository;
    private final ExamSessionRepository examSessionRepository;
    private final ExamSessionMapper examSessionMapper;
    private final StudentParticipationService participationService;
    private final StudentParticipationMapper participationMapper;

    public ExamSessionService(ExamRepository examRepository, ExamSessionRepository examSessionRepository, ExamSessionMapper examSessionMapper, StudentParticipationService participationService, StudentParticipationMapper participationMapper) {
        this.examRepository = examRepository;
        this.examSessionRepository = examSessionRepository;
        this.examSessionMapper = examSessionMapper;
        this.participationService = participationService;
        this.participationMapper = participationMapper;
    }

    @Transactional
    public List<ExamSessionDto> getExamSessions(String examId) {
        var tmp = examRepository.findByExamId(examId);
        if (tmp.isEmpty()) {
            log.info("Tried to GET ExamSessions for non-existing Exam [%s]".formatted(examId));
            throw new ResourceNotFoundException("Exam with id %s not found".formatted(examId));
        }
        var examSessions = examSessionRepository.findByExam(tmp.get());
        return examSessionMapper.toDto(examSessions);
    }

    @Transactional
    public ExamSessionDetailDto getExamSession(Long id) {
        var tmp = examSessionRepository.findById(id);
        if (tmp.isEmpty()) {
            log.info("Tried to GET non-existing ExamSession [%s]".formatted(id));
            throw new ResourceNotFoundException("ExamSession with id %s not found".formatted(id));
        }
        var examSession = tmp.get();
        return examSessionMapper.toDetailDto(examSession);
    }

    @Transactional
    public ExamSessionDto createExamSession(String examId, NewExamSessionDto sessionDto) {
        var tmpExam = examRepository.findByExamId(examId);
        if (tmpExam.isEmpty()) {
            log.info("Tried to CREATE ExamSession with non-existing Exam [%s]".formatted(examId));
            throw new ResourceNotFoundException("Exam with id %s not found".formatted(examId));
        }
        var exam = tmpExam.get();
        var tmp = examSessionRepository.findByExamIdAndClassRoomId(examId, sessionDto.getClassRoomId());
        if (tmp.isPresent()) {
            throw new NotUniqueException("ExamSession for exam [%s] in room %s already exists".formatted(examId, sessionDto.getClassRoomId()));
        }
        var examSession = examSessionMapper.toDomain(sessionDto);
        examSession.setExam(exam);
        examSession = examSessionRepository.save(examSession);
        return examSessionMapper.toDto(examSession);
    }

    @Transactional
    public ExamSessionDto updateExamSession(Long id, NewExamSessionDto sessionDto) {
        var tmpSession = examSessionRepository.findByIdWithExam(id);
        if (tmpSession.isEmpty()) {
            log.info("Tried to UPDATE non-existing ExamSession [%s]".formatted(id));
            throw new ResourceNotFoundException("ExamSession with id %s not found".formatted(id));
        }
        var examSession = tmpSession.get();
        var examId = examSession.getExam().getId();

        if (!examSession.getClassRoomId().equals(sessionDto.getClassRoomId())) {
            var tmp = examSessionRepository.findByExamIdAndClassRoomId(examId, sessionDto.getClassRoomId());
            if (tmp.isPresent()) {
                log.info("Tried to UPDATE ExamSession [%s] to conflicting classroom %s".formatted(id, sessionDto.getClassRoomId()));
                throw new NotUniqueException("ExamSession for exam %s in room %s already exists".formatted(examId, sessionDto.getClassRoomId()));
            }
        }

        examSession.setClassRoomId(sessionDto.getClassRoomId());
        examSession.setSupervisorName(sessionDto.getSupervisorName());
        examSession = examSessionRepository.save(examSession);

        return examSessionMapper.toDto(examSession);
    }

    @Transactional
    public void deleteExamSession(Long id) {
        if (!examSessionRepository.existsById(id)) {
            log.info("Tried to DELETE non-existing ExamSession [%s]".formatted(id));
            throw new ResourceNotFoundException("ExamSession with id %s not found".formatted(id));
        }
        examSessionRepository.deleteById(id);
    }

    @Transactional
    public List<StudentParticipationDto> getStudentParticipations(Long sessionId) {
        var tmp = examSessionRepository.findByIdWithParticipations(sessionId);
        if (tmp.isEmpty()) {
            log.info("Tried to GET StudentParticipations for non-existing ExamSession [%s]".formatted(sessionId));
            throw new ResourceNotFoundException("ExamSession with id %s not found".formatted(sessionId));
        }
        var examSession = tmp.get();
        return participationMapper.toDto(examSession.getParticipations());
    }

    @Transactional
    public void endExamSession(Long id) {
        var tmp = examSessionRepository.findByIdWithParticipations(id);
        if (tmp.isEmpty()) {
            log.info("Tried to END non-existing ExamSession [%s]".formatted(id));
            throw new ResourceNotFoundException("ExamSession with id %s not found".formatted(id));
        }
        var examSession = tmp.get();
        var participations = examSession.getParticipations();

        for (var participation: participations){
            //TODO: optimize this process
            participationService.endStudentParticipation(participation.getId());
        }
    }
}
