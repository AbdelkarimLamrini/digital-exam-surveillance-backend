package be.kdg.backend.client;

import be.kdg.backend.domain.Recording;
import be.kdg.backend.domain.StudentParticipation;

import java.util.List;

public interface StreamingClient {
    List<String> getPublishingStudentIds();

    StudentParticipation createStreamingProcess(StudentParticipation participation);

    Recording createRecordingProcess(Recording recording);

    void stopStreamingProcess(StudentParticipation participation);

    void stopRecordingProcess(Recording recording);

    void stopAllProcesses();
}
