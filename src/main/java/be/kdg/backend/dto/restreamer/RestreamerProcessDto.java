package be.kdg.backend.dto.restreamer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestreamerProcessDto {
    private String id;
    private String type;
    private String reference;
    private long created_at;
    private long updated_at;
}