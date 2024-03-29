package be.kdg.backend.mapper;

import be.kdg.backend.domain.ExamSession;
import be.kdg.backend.dto.examsession.ExamSessionDetailDto;
import be.kdg.backend.dto.examsession.ExamSessionDto;
import be.kdg.backend.dto.examsession.NewExamSessionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamSessionMapper {
    @Mapping(target = "examId", source = "exam.id")
    @Mapping(target = "examName", source = "exam.name")
    @Mapping(target = "startTime", source = "exam.startTime")
    @Mapping(target = "endTime", source = "exam.endTime")
    ExamSessionDetailDto toDetailDto(ExamSession examSession);

    ExamSessionDto toDto(ExamSession examSession);

    List<ExamSessionDto> toDto(List<ExamSession> examSessions);

    ExamSession toDomain(NewExamSessionDto examSessionDto);
}
