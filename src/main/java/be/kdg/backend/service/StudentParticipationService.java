package be.kdg.backend.service;

import be.kdg.backend.client.StreamingClient;
import be.kdg.backend.controller.SupervisorWsController;
import be.kdg.backend.domain.ConnectionStatus;
import be.kdg.backend.domain.StreamLog;
import be.kdg.backend.dto.recording.RecordingDto;
import be.kdg.backend.dto.streamlog.StreamLogDto;
import be.kdg.backend.dto.student.NewStudentParticipationDto;
import be.kdg.backend.dto.student.StudentParticipationAIDto;
import be.kdg.backend.dto.student.StudentParticipationDto;
import be.kdg.backend.dto.student.StudentParticipationFlutterDto;
import be.kdg.backend.exception.ResourceNotFoundException;
import be.kdg.backend.mapper.RecordingMapper;
import be.kdg.backend.mapper.StreamLogMapper;
import be.kdg.backend.mapper.StudentParticipationMapper;
import be.kdg.backend.repository.ExamSessionRepository;
import be.kdg.backend.repository.RecordingRepository;
import be.kdg.backend.repository.StreamLogRepository;
import be.kdg.backend.repository.StudentParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StudentParticipationService {
    private final SupervisorWsController supervisorWsController;
    private final StreamingClient streamingClient;
    private final StudentParticipationMapper participationMapper;
    private final StudentParticipationRepository participationRepository;
    private final RecordingRepository recordingRepository;
    private final ExamSessionRepository examSessionRepository;
    private final RecordingMapper recordingMapper;
    private final StreamLogRepository streamLogRepository;
    private final StreamLogMapper streamLogMapper;

    public StudentParticipationService(SupervisorWsController supervisorWsController, StreamingClient streamingClient, StudentParticipationMapper participationMapper, StudentParticipationRepository participationRepository, RecordingRepository recordingRepository, ExamSessionRepository examSessionRepository, RecordingMapper recordingMapper, StreamLogRepository streamLogRepository, StreamLogMapper streamLogMapper) {
        this.supervisorWsController = supervisorWsController;
        this.streamingClient = streamingClient;
        this.participationMapper = participationMapper;
        this.participationRepository = participationRepository;
        this.recordingRepository = recordingRepository;
        this.examSessionRepository = examSessionRepository;
        this.recordingMapper = recordingMapper;
        this.streamLogRepository = streamLogRepository;
        this.streamLogMapper = streamLogMapper;
    }

    @Transactional
    public StudentParticipationDto getStudentParticipation(Long id) {
        var tmp = participationRepository.findById(id);
        if (tmp.isEmpty()) {
            log.info("Tried to GET non-existing StudentParticipation [%s]".formatted(id));
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(id));
        }
        return participationMapper.toDto(tmp.get());
    }

    @Transactional
    public StudentParticipationFlutterDto registerStudentParticipation(NewStudentParticipationDto participationDto) {
        var examId = participationDto.getExamId();
        var classRoomId = participationDto.getClassRoomId();
        var tmpExamSession = examSessionRepository.findByExamIdAndClassRoomId(examId, classRoomId);
        if (tmpExamSession.isEmpty()) {
            log.info("Tried to REGISTER StudentParticipation for non-existing ExamSession [%s, %s]".formatted(examId, classRoomId));
            throw new ResourceNotFoundException("ExamSession with id %s in classroom %s not found".formatted(examId, classRoomId));
        }
        var examSession = tmpExamSession.get();

        var participation = participationRepository.findByExamSessionAndStudentId(examSession.id, participationDto.getStudentId()).orElseGet(() -> participationMapper.toDomain(participationDto));

        participation.setExamSession(examSession);
        participation.setStatus(ConnectionStatus.CONNECTING);
        participation = participationRepository.save(participation);
        participation = streamingClient.createStreamingProcess(participation);
        participation = participationRepository.save(participation);

        var streamLog = new StreamLog(participation, LocalDateTime.now(), ConnectionStatus.CONNECTING);
        streamLogRepository.save(streamLog);

        supervisorWsController.sendParticipationMessage(examSession.getId(), participationMapper.toDto(participation));
        return participationMapper.toFlutterDto(participation);
    }

    @Transactional
    public List<StudentParticipationAIDto> getActiveStudentParticipations() {
        var participations = participationRepository.findByActiveExamAndNotTerminated();
        return participationMapper.toAiDto(participations);
    }

    @Transactional
    public void endStudentParticipation(Long id) {
        var tmp = participationRepository.findById(id);
        if (tmp.isEmpty()) {
            log.info("Tried to END non-existing StudentParticipation [%s]".formatted(id));
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(id));
        }
        var participation = tmp.get();

        streamingClient.stopStreamingProcess(participation);
        participation.setStatus(ConnectionStatus.TERMINATED);
        participation.setEndTime(LocalDateTime.now());
        participation = participationRepository.save(participation);

        var recordings = recordingRepository.findByStudentParticipationIdAndEndTimeIsNull(id);

        for (var recording : recordings) {
            recording.setEndTime(LocalDateTime.now());
            recording.setStudentParticipation(participation);
            streamingClient.stopRecordingProcess(recording);
            recordingRepository.save(recording);
        }

        var streamLog = new StreamLog(participation, LocalDateTime.now(), ConnectionStatus.TERMINATED);
        streamLogRepository.save(streamLog);

        supervisorWsController.sendParticipationMessage(participation.getExamSession().getId(), participationMapper.toDto(participation));
    }

    @Transactional
    public List<RecordingDto> getRecordings(Long id) {
        var tmp = participationRepository.findByIdWithRecordings(id);
        if (tmp.isEmpty()) {
            log.info("Tried to GET Recordings for non-existing StudentParticipation [%s]".formatted(id));
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(id));
        }
        var participation = tmp.get();
        return recordingMapper.toDto(participation.getRecordings());
    }

    @Transactional
    public List<StreamLogDto> getLogsForStudentParticipation(Long id) {
        var tmp = participationRepository.findById(id);
        if (tmp.isEmpty()) {
            log.info("Tried to GET Logs for non-existing StudentParticipation [%s]".formatted(id));
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(id));
        }
        var participation = tmp.get();
        var logs = streamLogRepository.findByStudentParticipationOrderByTimestamp(participation);
        return streamLogMapper.toDto(logs);
    }
}
