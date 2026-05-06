package com.hostelpay.repositories;

import com.hostelpay.entities.Hostel;
import com.hostelpay.entities.Student;
import com.hostelpay.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserRepository userRepository;

    private User testOwner;
    private Hostel testHostel;
    private Student testStudent;

    @BeforeEach
    public void setUp() {
        testOwner = User.builder()
            .email("owner@example.com")
            .phoneNumber("9876543210")
            .passwordHash("hashed_password_123")
            .role(User.UserRole.OWNER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        testOwner = userRepository.save(testOwner);

        testHostel = Hostel.builder()
            .owner(testOwner)
            .name("Test Hostel")
            .address("123 Main Street")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        testHostel = hostelRepository.save(testHostel);

        testStudent = Student.builder()
            .hostel(testHostel)
            .fullName("John Doe")
            .phoneNumber("9000000001")
            .dob(LocalDate.of(2000, 5, 15))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    public void testSaveStudent() {
        Student savedStudent = studentRepository.save(testStudent);

        assertNotNull(savedStudent.getId());
        assertEquals("John Doe", savedStudent.getFullName());
        assertEquals(testHostel.getId(), savedStudent.getHostel().getId());
    }

    @Test
    public void testFindStudentsByHostelId() {
        studentRepository.save(testStudent);

        List<Student> students = studentRepository.findByHostelId(testHostel.getId());

        assertTrue(students.size() > 0);
        assertEquals("John Doe", students.get(0).getFullName());
    }

    @Test
    public void testFindActiveStudentsFilter() {
        Student savedStudent = studentRepository.save(testStudent);
        studentRepository.flush();

        savedStudent.setIsActive(false);
        studentRepository.save(savedStudent);
        studentRepository.flush();

        List<Student> activeStudents = studentRepository.findActiveStudentsByHostelId(testHostel.getId());

        assertEquals(0, activeStudents.size());
    }

    @Test
    public void testMultiTenancyIsolation() {
        Student savedStudent = studentRepository.save(testStudent);

        User otherOwner = User.builder()
            .email("other_owner@example.com")
            .phoneNumber("9111111111")
            .passwordHash("hashed_password_456")
            .role(User.UserRole.OWNER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        otherOwner = userRepository.save(otherOwner);

        Hostel otherHostel = Hostel.builder()
            .owner(otherOwner)
            .name("Other Hostel")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        otherHostel = hostelRepository.save(otherHostel);

        List<Student> studentsInOtherHostel = studentRepository.findByHostelId(otherHostel.getId());

        assertEquals(0, studentsInOtherHostel.size());
    }

}
