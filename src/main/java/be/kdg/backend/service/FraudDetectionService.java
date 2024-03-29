package be.kdg.backend.service;

import be.kdg.backend.controller.SupervisorWsController;
import be.kdg.backend.domain.FraudDetection;
import be.kdg.backend.dto.fraud.FraudDetectionDto;
import be.kdg.backend.dto.fraud.NewFraudDetectionDto;
import be.kdg.backend.exception.ResourceNotFoundException;
import be.kdg.backend.mapper.FraudDetectionMapper;
import be.kdg.backend.repository.FraudDetectionRepository;
import be.kdg.backend.repository.StudentParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FraudDetectionService {
    private final StudentParticipationRepository participationRepository;
    private final FraudDetectionRepository fraudDetectionRepository;
    private final FraudDetectionMapper fraudDetectionMapper;
    private final SupervisorWsController supervisorWsController;

    public FraudDetectionService(StudentParticipationRepository participationRepository, FraudDetectionRepository fraudDetectionRepository, FraudDetectionMapper fraudDetectionMapper, SupervisorWsController supervisorWsController) {
        this.participationRepository = participationRepository;
        this.fraudDetectionRepository = fraudDetectionRepository;
        this.fraudDetectionMapper = fraudDetectionMapper;
        this.supervisorWsController = supervisorWsController;
    }

    @Transactional
    public void detectFraud(NewFraudDetectionDto newFraudDetectionDto) {
        var participationId = newFraudDetectionDto.getParticipationId();
        var tmpParticipation = participationRepository.findByIdWithExamSession(participationId);
        if (tmpParticipation.isEmpty()) {
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(participationId));
        }
        var participation = tmpParticipation.get();
        var examSession = participation.getExamSession();
        var fraudDetection = new FraudDetection(participation, newFraudDetectionDto.getFraudScore());
        fraudDetection = fraudDetectionRepository.save(fraudDetection);

        supervisorWsController.sendFraudDetection(examSession.getId(), fraudDetectionMapper.toDto(fraudDetection));
    }

    @Transactional
    public List<FraudDetectionDto> getFraudDetectionsForParticipation(Long participationId) {
        if (!participationRepository.existsById(participationId)) {
            log.info("Tried to GET FraudDetections for non-existing StudentParticipation [%s]".formatted(participationId));
            throw new ResourceNotFoundException("StudentParticipation with id %s not found".formatted(participationId));
        }

        return fraudDetectionMapper.toDto(fraudDetectionRepository.findByStudentParticipationId(participationId));
    }
}
