package com.hostelpay.repositories;

import com.hostelpay.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.isActive = true")
    List<Room> findActiveRoomsByHostelId(@Param("hostelId") UUID hostelId);

    boolean existsByHostelIdAndRoomNumber(UUID hostelId, String roomNumber);
}
