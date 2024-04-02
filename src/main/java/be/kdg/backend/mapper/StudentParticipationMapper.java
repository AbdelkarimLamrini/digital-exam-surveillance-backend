package be.kdg.backend.mapper;

import be.kdg.backend.domain.StudentParticipation;
import be.kdg.backend.dto.student.NewStudentParticipationDto;
import be.kdg.backend.dto.student.StudentParticipationAIDto;
import be.kdg.backend.dto.student.StudentParticipationDto;
import be.kdg.backend.dto.student.StudentParticipationFlutterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentParticipationMapper {
    StudentParticipation toDomain(NewStudentParticipationDto participationDto);

    @Mapping(target = "classRoomId", source = "examSession.classRoomId")
    @Mapping(target = "examId", source = "examSession.exam.id")
    StudentParticipationDto toDto(StudentParticipation participation);

    List<StudentParticipationDto> toDto(List<StudentParticipation> participations);

    List<StudentParticipationAIDto> toAiDto(List<StudentParticipation> participations);

    @Mapping(target = "classRoomId", source = "examSession.classRoomId")
    @Mapping(target = "examId", source = "examSession.exam.id")
    StudentParticipationFlutterDto toFlutterDto(StudentParticipation participation);
}
