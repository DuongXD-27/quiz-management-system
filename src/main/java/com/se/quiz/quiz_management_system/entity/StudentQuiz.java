package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "student_quiz_result",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "quiz_id"})
        }
)
public class StudentQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private Double result;
}
