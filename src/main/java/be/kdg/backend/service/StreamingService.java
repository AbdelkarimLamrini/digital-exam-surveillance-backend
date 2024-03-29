package be.kdg.backend.service;

import be.kdg.backend.client.StreamingClient;
import be.kdg.backend.controller.SupervisorWsController;
import be.kdg.backend.domain.ConnectionStatus;
import be.kdg.backend.domain.StreamLog;
import be.kdg.backend.mapper.StreamLogMapper;
import be.kdg.backend.message.StreamLogMessage;
import be.kdg.backend.repository.ExamSessionRepository;
import be.kdg.backend.repository.StreamLogRepository;
import be.kdg.backend.repository.StudentParticipationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class StreamingService {
    private final SupervisorWsController supervisorWsController;
    private final StreamingClient streamingClient;
    private final StreamLogRepository streamLogRepository;
    private final StudentParticipationRepository studentParticipationRepository;
    private final ExamSessionRepository examSessionRepository;
    private final StreamLogMapper streamLogMapper;

    public StreamingService(SupervisorWsController supervisorWsController, StreamingClient streamingClient, StreamLogRepository streamLogRepository, StudentParticipationRepository studentParticipationRepository, ExamSessionRepository examSessionRepository, StreamLogMapper streamLogMapper) {
        this.supervisorWsController = supervisorWsController;
        this.streamingClient = streamingClient;
        this.streamLogRepository = streamLogRepository;
        this.studentParticipationRepository = studentParticipationRepository;
        this.examSessionRepository = examSessionRepository;
        this.streamLogMapper = streamLogMapper;
    }

    public void stopAllProcesses() {
        streamingClient.stopAllProcesses();
    }

    /**
     * Probes all active exam sessions for active streams.<br><br>
     * This method runs every x seconds and calls the SupervisorWsController::sendToExamSession,
     * where x is defined in the application.properties file under app.probe-interval_seconds.
     */
    @Scheduled(cron = "*/${app.options.probe-interval_seconds} * * * * *")
    public void probeAllStreams() {
        var activeSessions = examSessionRepository.findActiveSessions();
        var activeSessionIds = new ArrayList<Long>();
        var messageMap = new HashMap<Long, List<StreamLogMessage>>();
        for (var session: activeSessions) {
            activeSessionIds.add(session.getId());
            messageMap.put(session.getId(), new ArrayList<>());
        }

        var activeStudents = studentParticipationRepository.findByExamSessionIdInAndNotTerminated(activeSessionIds);
        var publishingStudentIds = streamingClient.getPublishingStudentIds();

        var logs = new ArrayList<StreamLog>();
        var currentTimestamp = LocalDateTime.now();
        for (var sp : activeStudents) {
            var streamLog = new StreamLog(sp, currentTimestamp);
            if (publishingStudentIds.contains(sp.getStudentId())) {
                streamLog.setStatus(ConnectionStatus.CONNECTED);
                sp.setStatus(ConnectionStatus.CONNECTED);
            } else {
                streamLog.setStatus(ConnectionStatus.DISCONNECTED);
                sp.setStatus(ConnectionStatus.DISCONNECTED);
            }
            logs.add(streamLog);
            messageMap.get(sp.getExamSession().getId()).add(streamLogMapper.toMessage(streamLog));
        }

        streamLogRepository.saveAll(logs);
        studentParticipationRepository.saveAll(activeStudents);
        messageMap.forEach(supervisorWsController::sendLogMessage);
    }
}
