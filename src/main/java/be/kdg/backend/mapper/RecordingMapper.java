package be.kdg.backend.mapper;

import be.kdg.backend.domain.Recording;
import be.kdg.backend.dto.recording.RecordingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecordingMapper {
    @Mapping(target = "studentParticipationId", source = "studentParticipation.id")
    @Mapping(target = "studentId", source = "studentParticipation.studentId")
    RecordingDto toDto(Recording recording);

    List<RecordingDto> toDto(List<Recording> recordings);
}
