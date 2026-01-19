package com.webapp.hexit.repository;

import com.webapp.hexit.model.LessonBooking;
import com.webapp.hexit.model.Muzikant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonBookingRepository
  extends JpaRepository<LessonBooking, Long>
{
  // Find all bookings for a specific lesson (for teachers to see all)
  List<LessonBooking> findByLessonId(Long lessonId);

  // Find bookings for a specific lesson by a specific student
  List<LessonBooking> findByLessonIdAndStudent(Long lessonId, Muzikant student);

  // Find a specific booking by lesson and student
  Optional<LessonBooking> findByLessonIdAndStudentId(
    Long lessonId,
    Long studentId
  );

  // Find all bookings for a student
  List<LessonBooking> findByStudentId(Long studentId);

  // Find booking by id
  Optional<LessonBooking> findById(Long id);
}
