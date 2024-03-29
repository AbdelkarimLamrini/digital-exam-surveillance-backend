package be.kdg.backend.message;

import be.kdg.backend.domain.ConnectionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StreamLogMessage {
    private String participationId;
    private LocalDateTime timestamp;
    private ConnectionStatus status;
}
