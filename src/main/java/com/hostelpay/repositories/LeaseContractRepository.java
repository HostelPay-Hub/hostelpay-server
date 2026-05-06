package com.hostelpay.repositories;

import com.hostelpay.entities.LeaseContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaseContractRepository extends JpaRepository<LeaseContract, UUID> {

    @Query("SELECT lc FROM LeaseContract lc WHERE lc.hostel.id = :hostelId AND lc.isActive = true")
    List<LeaseContract> findActiveLeasesByHostelId(@Param("hostelId") UUID hostelId);

    @Query("SELECT lc FROM LeaseContract lc WHERE lc.student.id = :studentId AND lc.isActive = true")
    List<LeaseContract> findActiveLeasesByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT lc FROM LeaseContract lc WHERE lc.hostel.id = :hostelId AND lc.isActive = true AND lc.endDate IS NULL")
    List<LeaseContract> findCurrentActiveLeasesByHostelId(@Param("hostelId") UUID hostelId);
    @Query("SELECT COUNT(l) FROM LeaseContract l WHERE l.room.id = :roomId AND l.isActive = true")
    long countActiveLeasesByRoomId(@Param("roomId") UUID roomId);

    @Modifying
    @Query("UPDATE LeaseContract l SET l.isActive = false WHERE l.student.id = :studentId")
    void deactivateAllLeasesForStudent(@Param("studentId") UUID studentId);
}
