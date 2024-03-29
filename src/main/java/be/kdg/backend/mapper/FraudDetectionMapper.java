package be.kdg.backend.mapper;

import be.kdg.backend.domain.FraudDetection;
import be.kdg.backend.dto.fraud.FraudDetectionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FraudDetectionMapper {
    @Mapping(target = "participationId", source = "studentParticipation.id")
    @Mapping(target = "studentId", source = "studentParticipation.studentId")
    FraudDetectionDto toDto(FraudDetection fraudDetection);

    List<FraudDetectionDto> toDto(List<FraudDetection> fraudDetections);
}
