package be.kdg.backend.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id", "class_room_id"}))
public class ExamSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne
    public Exam exam;
    @OneToMany(mappedBy = "examSession")
    public List<StudentParticipation> participations;
    public String classRoomId;
    public String supervisorName;
}
