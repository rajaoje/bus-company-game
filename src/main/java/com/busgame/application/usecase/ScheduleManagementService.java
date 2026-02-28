// application/usecase/ScheduleManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.exception.*;
import com.busgame.domain.model.*;
import com.busgame.domain.port.in.ScheduleManagementUseCase;
import com.busgame.domain.port.out.*;
import com.busgame.domain.service.ScheduleValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use Case de planification des horaires.
 *
 * C'est la classe la plus "lourde" qu'on ait ecrite jusqu'ici,
 * parce qu'elle coordonne quatre repositories et un Domain Service.
 * Mais remarque que chaque etape reste simple et lisible —
 * la complexite est distribuee, pas concentree.
 */
@Service
@Transactional
public class ScheduleManagementService implements ScheduleManagementUseCase {

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;

    // Le Domain Service est instancie directement — pas besoin de Spring ici
    // puisqu'il n'a aucune dependance externe.
    private final ScheduleValidationService validationService = new ScheduleValidationService();

    public ScheduleManagementService(ScheduleRepository scheduleRepository,
                                     BusRepository busRepository,
                                     DriverRepository driverRepository,
                                     RouteRepository routeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public Schedule planSchedule(BusId busId, DriverId driverId, RouteId routeId,
                                 LocalDateTime startTime, LocalDateTime endTime) {

        // Etape 1 : verifier que les trois entites existent
        // Si l'une d'elles n'existe pas, on echoue immediatement avec une erreur claire.
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException(busId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));

        // On verifie que le parcours existe — on n'a pas besoin de l'objet complet,
        // juste de savoir qu'il est valide. On le recupere quand meme pour etre coherent.
        routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        // Etape 2 : calculer la duree du service pour la validation des heures
        double durationHours = java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;

        // Etape 3 : valider la disponibilite via le Domain Service
        // Ces appels lancent des IllegalStateException si les regles ne sont pas respectees.
        validationService.validateBusAvailability(bus);
        validationService.validateDriverAvailability(driver, durationHours);

        // Etape 4 : verifier les chevauchements d'horaires
        // C'est ici que la difference entre Domain Service et Use Case est visible :
        // cette verification necessite d'interroger le repository (infrastructure),
        // donc elle ne peut pas etre dans le domaine pur — elle est dans le Use Case.
        checkBusOverlap(busId, startTime, endTime);
        checkDriverOverlap(driverId, startTime, endTime);

        // Etape 5 : creer et sauvegarder l'horaire
        Schedule schedule = Schedule.plan(busId, driverId, routeId, startTime, endTime);
        return scheduleRepository.save(schedule);
    }

    private void checkBusOverlap(BusId busId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Schedule> conflicts = scheduleRepository
                .findActiveSchedulesForBusInPeriod(busId, startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException(
                    "Le bus a deja un horaire sur cette plage horaire."
            );
        }
    }

    private void checkDriverOverlap(DriverId driverId,
                                    LocalDateTime startTime, LocalDateTime endTime) {
        List<Schedule> conflicts = scheduleRepository
                .findActiveSchedulesForDriverInPeriod(driverId, startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException(
                    "Le conducteur a deja un horaire sur cette plage horaire."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getSchedule(ScheduleId id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesForBus(BusId busId) {
        return scheduleRepository.findByBusId(busId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesForDriver(DriverId driverId) {
        return scheduleRepository.findByDriverId(driverId);
    }

    @Override
    public Schedule cancelSchedule(ScheduleId id) {
        Schedule schedule = getSchedule(id);
        schedule.cancel();
        return scheduleRepository.save(schedule);
    }
}