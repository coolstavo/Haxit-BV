package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lesson_bookings")
public class LessonBooking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "muzikant_id", nullable = false)
  private Muzikant student;

  @ManyToOne
  @JoinColumn(name = "lesson_id", nullable = false)
  private Lesson lesson;

  // Status voor het algemene proces (bijv. AANGEVRAAGD, DEFINITIEF, AFGEWEZEN)
  @Enumerated(EnumType.STRING)
  private LessonStatus status = LessonStatus.AANGEVRAAGD;

  // De "Dubbele Handdruk" velden
  @Column(nullable = false)
  private boolean acceptedByStudent = false;

  @Column(nullable = false)
  private boolean acceptedByDocent = false;

  @ElementCollection
  @CollectionTable(
    name = "lesson_booking_proposals",
    joinColumns = @JoinColumn(name = "booking_id")
  )
  @Column(name = "proposed_time")
  private List<LocalDateTime> lessonProposals;

  private LocalDateTime confirmedTime;

  private LocalDateTime createdAt;

  public LessonBooking() {
    this.createdAt = LocalDateTime.now();
  }

  // Helper methode om te zien of de afspraak volledig rond is
  public boolean isConfirmed() {
    return acceptedByStudent && acceptedByDocent;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public Muzikant getStudent() {
    return student;
  }

  public void setStudent(Muzikant student) {
    this.student = student;
  }

  public Lesson getLesson() {
    return lesson;
  }

  public void setLesson(Lesson lesson) {
    this.lesson = lesson;
  }

  public LessonStatus getStatus() {
    return status;
  }

  public void setStatus(LessonStatus status) {
    this.status = status;
  }

  public boolean isAcceptedByStudent() {
    return acceptedByStudent;
  }

  public void setAcceptedByStudent(boolean acceptedByStudent) {
    this.acceptedByStudent = acceptedByStudent;
  }

  public boolean isAcceptedByDocent() {
    return acceptedByDocent;
  }

  public void setAcceptedByDocent(boolean acceptedByDocent) {
    this.acceptedByDocent = acceptedByDocent;
  }

  public List<LocalDateTime> getLessonProposals() {
    return lessonProposals;
  }

  public void setLessonProposals(List<LocalDateTime> lessonProposals) {
    this.lessonProposals = lessonProposals;
  }

  public LocalDateTime getConfirmedTime() {
    return confirmedTime;
  }

  public void setConfirmedTime(LocalDateTime confirmedTime) {
    this.confirmedTime = confirmedTime;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
