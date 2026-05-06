package com.hostelpay.repositories;

import com.hostelpay.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    @Query("SELECT s FROM Student s WHERE s.hostel.id = :hostelId AND s.isActive = true")
    List<Student> findActiveStudentsByHostelId(@Param("hostelId") UUID hostelId);

    Optional<Student> findByPhoneNumberAndDob(String phoneNumber, java.time.LocalDate dob);

    Optional<Student> findByUserId(UUID userId);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.hostel.id = :hostelId AND s.isActive = true")
    long countActiveStudentsByHostelId(@Param("hostelId") UUID hostelId);
}
