package be.kdg.backend.mapper;

import be.kdg.backend.domain.StreamLog;
import be.kdg.backend.dto.streamlog.StreamLogDto;
import be.kdg.backend.message.StreamLogMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StreamLogMapper {
    @Mapping(target = "participationId", source = "studentParticipation.id")
    StreamLogDto toDto(StreamLog streamLog);

    List<StreamLogDto> toDto(List<StreamLog> streamLogs);

    @Mapping(target = "participationId", source = "studentParticipation.id")
    StreamLogMessage toMessage(StreamLog streamLog);
}
