package be.kdg.backend.mapper;

import be.kdg.backend.domain.Exam;
import be.kdg.backend.dto.exam.ExamDetailDto;
import be.kdg.backend.dto.exam.ExamDto;
import be.kdg.backend.dto.exam.NewExamDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ExamSessionMapper.class})
public interface ExamMapper {
    ExamDto toDto(Exam exam);

    ExamDetailDto toDetailDto(Exam exam);

    Exam toDomain(NewExamDto examDto);

    List<ExamDto> toDto(List<Exam> exams);
}
