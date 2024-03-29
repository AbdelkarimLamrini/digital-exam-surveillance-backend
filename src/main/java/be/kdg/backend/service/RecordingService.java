package be.kdg.backend.service;

import be.kdg.backend.client.StreamingClient;
import be.kdg.backend.domain.Recording;
import be.kdg.backend.dto.recording.RecordingDto;
import be.kdg.backend.exception.ResourceNotFoundException;
import be.kdg.backend.mapper.RecordingMapper;
import be.kdg.backend.repository.RecordingRepository;
import be.kdg.backend.repository.StudentParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class RecordingService {
    private final StreamingClient streamingClient;
    private final StudentParticipationRepository participationRepository;
    private final RecordingRepository recordingRepository;
    private final RecordingMapper recordingMapper;

    public RecordingService(StreamingClient streamingClient, StudentParticipationRepository participationRepository, RecordingRepository recordingRepository, RecordingMapper recordingMapper) {
        this.streamingClient = streamingClient;
        this.participationRepository = participationRepository;
        this.recordingRepository = recordingRepository;
        this.recordingMapper = recordingMapper;
    }

    @Transactional
    public RecordingDto createRecording(Long participationId) {
        var tmpParticipation = participationRepository.findById(participationId);
        if (tmpParticipation.isEmpty()) {
            log.info("Tried to CREATE Recording for non-existing StudentParticipation [%s]".formatted(participationId));
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(participationId));
        }
        var participation = tmpParticipation.get();

        var tmpRecording = recordingRepository.findLastByStudentParticipationId(participationId);
        if (tmpRecording.isPresent() && tmpRecording.get().getEndTime() == null) {
            log.info("Tried to CREATE Recording for StudentParticipation [%s] while a Recording is already running".formatted(participationId));
            throw new IllegalStateException("Recording for StudentParticipation with id %s is already running".formatted(participationId));
        }

        var recording = new Recording();
        recording.setStudentParticipation(participation);
        recording = recordingRepository.save(recording);
        recording = streamingClient.createRecordingProcess(recording);
        recording = recordingRepository.save(recording);
        participation.setRecording(true);
        participationRepository.save(participation);

        return recordingMapper.toDto(recording);
    }

    @Transactional
    public void endRecording(Long id) {
        var tmp = recordingRepository.findByIdWithParticipation(id);
        if (tmp.isEmpty()) {
            log.info("Tried to END non-existing Recording [%s]".formatted(id));
            throw new ResourceNotFoundException("Recording with id %s not found".formatted(id));
        }
        var recording = tmp.get();

        if (recording.getEndTime() != null) {
            log.info("Tried to END Recording [%s] while it is already stopped".formatted(id));
            throw new IllegalStateException("Recording with id %s is already stopped".formatted(id));
        }

        streamingClient.stopRecordingProcess(recording);
        recording.setEndTime(LocalDateTime.now());
        recordingRepository.save(recording);
        var participation = recording.getStudentParticipation();
        participation.setRecording(false);
        participationRepository.save(participation);
    }

    @Transactional
    public RecordingDto endRecordingForParticipation(Long participationId) {
        var tmpParticipation = participationRepository.findById(participationId);
        if (tmpParticipation.isEmpty()) {
            log.info("Tried to END Recording for non-existing StudentParticipation [%s]".formatted(participationId));
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(participationId));
        }
        var participation = tmpParticipation.get();
        var tmpRecording = recordingRepository.findLastByStudentParticipationId(participationId);
        if (tmpRecording.isEmpty()) {
            log.info("Tried to END non-existing Recording for StudentParticipation [%s]".formatted(participationId));
            throw new ResourceNotFoundException("Recording for StudentParticipation with id %s not found".formatted(participationId));
        }
        var recording = tmpRecording.get();

        if (recording.getEndTime() != null) {
            log.info("Tried to END Recording for StudentParticipation [%s] while it is already stopped".formatted(participationId));
            throw new IllegalStateException("Recording for StudentParticipation with id %s is already stopped".formatted(participationId));
        }

        streamingClient.stopRecordingProcess(recording);
        recording.setEndTime(LocalDateTime.now());
        recordingRepository.save(recording);
        participation.setRecording(false);
        participationRepository.save(participation);

        return recordingMapper.toDto(recording);
    }
}
