package be.kdg.backend.dto.streamlog;

import be.kdg.backend.domain.ConnectionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StreamLogDto {
    private Long id;
    private String participationId;
    private LocalDateTime timestamp;
    private ConnectionStatus status;
}
