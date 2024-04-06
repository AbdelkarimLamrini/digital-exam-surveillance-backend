package be.kdg.backend.controller;

import be.kdg.backend.dto.fraud.FraudDetectionDto;
import be.kdg.backend.dto.student.StudentParticipationDto;
import be.kdg.backend.message.StreamLogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
public class SupervisorWsController {
    private final SimpMessagingTemplate messagingTemplate;

    public SupervisorWsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Sends a StreamLog message to all supervisors of a specific exam session.<br><br>
     * This method is called by the StreamingService every x seconds,
     * where x is defined in the application.properties file under app.probe-interval-seconds.
     *
     * @param sessionId The id of the exam session to send the message to.
     * @param message   The List of StreamLogMessages to send.
     */
    public void sendLogMessage(Long sessionId, List<StreamLogMessage> message) {
        log.info("[WS] Sending log message to supervisors of ExamSession [%s]".formatted(sessionId));
        messagingTemplate.convertAndSend("/topic/exam-sessions/%s/logs".formatted(sessionId), message);
    }

    /**
     * Sends a StudentParticipation message to all supervisors of a specific exam session.<br><br>
     * This method is called by the StudentParticipationService whenever
     * a student joins or leaves an exam session.
     *
     * @param sessionId The id of the exam session to send the message to.
     * @param message   The StudentParticipationDto to send.
     */
    public void sendParticipationMessage(Long sessionId, StudentParticipationDto message) {
        log.info("[WS] Sending participation message to supervisors of ExamSession [%s]".formatted(sessionId));
        messagingTemplate.convertAndSend("/topic/exam-sessions/%s/participations".formatted(sessionId), message);
    }

    /**
     * Sends a FraudDetection message to all supervisors of a specific exam session.<br><br>
     * This method is called by the FraudDetectionService whenever
     * a fraud detection is detected.
     *
     * @param sessionId The id of the exam session to send the message to.
     * @param message   The FraudDetection to send.
     */
    public void sendFraudDetection(Long sessionId, FraudDetectionDto message) {
        log.info("[WS] Sending fraud detection message to supervisors of ExamSession [%s]".formatted(sessionId));
        messagingTemplate.convertAndSend("/topic/exam-sessions/%s/fraud-detections".formatted(sessionId), message);
    }
}
