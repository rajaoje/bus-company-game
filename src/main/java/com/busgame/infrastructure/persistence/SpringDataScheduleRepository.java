// infrastructure/persistence/SpringDataScheduleRepository.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataScheduleRepository extends JpaRepository<ScheduleJpaEntity, UUID> {

    List<ScheduleJpaEntity> findByBusId(UUID busId);
    List<ScheduleJpaEntity> findByDriverId(UUID driverId);

    /**
     * La requete JPQL de detection de chevauchement.
     * La condition "start < otherEnd AND end > otherStart" est la formule
     * classique de detection de chevauchement entre deux intervalles.
     * On filtre aussi sur les statuts actifs (PLANNED, IN_PROGRESS) —
     * un horaire CANCELLED ou COMPLETED ne bloque plus les ressources.
     */
    @Query("""
        SELECT s FROM ScheduleJpaEntity s
        WHERE s.busId = :busId
        AND s.status IN ('PLANNED', 'IN_PROGRESS')
        AND s.startTime < :endTime
        AND s.endTime > :startTime
    """)
    List<ScheduleJpaEntity> findActiveForBusInPeriod(
            @Param("busId") UUID busId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
        SELECT s FROM ScheduleJpaEntity s
        WHERE s.driverId = :driverId
        AND s.status IN ('PLANNED', 'IN_PROGRESS')
        AND s.startTime < :endTime
        AND s.endTime > :startTime
    """)
    List<ScheduleJpaEntity> findActiveForDriverInPeriod(
            @Param("driverId") UUID driverId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // Horaires PLANNED dont l'heure de debut est passee
    @Query("""
    SELECT s FROM ScheduleJpaEntity s
    WHERE s.status = 'PLANNED'
    AND s.startTime <= :gameTime
""")
    List<ScheduleJpaEntity> findSchedulesToStart(
            @Param("gameTime") LocalDateTime gameTime);

    // Horaires IN_PROGRESS dont l'heure de fin est passee
    @Query("""
    SELECT s FROM ScheduleJpaEntity s
    WHERE s.status = 'IN_PROGRESS'
    AND s.endTime <= :gameTime
""")
    List<ScheduleJpaEntity> findSchedulesToComplete(
            @Param("gameTime") LocalDateTime gameTime);

    // Tous les horaires avec un statut donne
    List<ScheduleJpaEntity> findByStatus(ScheduleStatus status);
}