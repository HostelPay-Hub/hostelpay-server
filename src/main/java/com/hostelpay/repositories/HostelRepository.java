package com.hostelpay.repositories;

import com.hostelpay.entities.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, UUID> {
    List<Hostel> findByOwnerId(UUID ownerId);

    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId AND h.isActive = true")
    List<Hostel> findActiveHostelsByOwnerId(@Param("ownerId") UUID ownerId);

    @Query(value = "SELECT * FROM hostels WHERE is_active = true", nativeQuery = true)
    List<Hostel> findAllActiveHostels();

    @Query(value = "SELECT * FROM hostels", nativeQuery = true)
    List<Hostel> findAllHostelsIncludingInactive();

    @Query(value = "SELECT * FROM hostels WHERE id = :id", nativeQuery = true)
    Optional<Hostel> findByIdIncludingInactive(@Param("id") UUID id);
}
