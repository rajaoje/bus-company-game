// application/handler/ScheduleStartedHandler.java
package com.busgame.application.handler;

import com.busgame.domain.event.ScheduleStartedEvent;
import com.busgame.domain.model.Bus;
import com.busgame.domain.model.Driver;
import com.busgame.domain.model.Schedule;
import com.busgame.domain.port.out.BusRepository;
import com.busgame.domain.port.out.DriverRepository;
import com.busgame.domain.port.out.ScheduleRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reagit au demarrage d'un horaire.
 * Responsabilites :
 * - Passer le statut de l'horaire a IN_PROGRESS
 * - Passer le bus a IN_SERVICE
 * - Passer le conducteur a ON_DUTY
 *
 * Chaque handler a sa propre transaction — si ce handler echoue,
 * les autres handlers du meme tick ne sont pas affectes.
 */
@Component
public class ScheduleStartedHandler {

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;

    public ScheduleStartedHandler(ScheduleRepository scheduleRepository,
                                  BusRepository busRepository,
                                  DriverRepository driverRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
    }

    @EventListener
    @Transactional
    public void handle(ScheduleStartedEvent event) {
        // Mettre a jour l'horaire
        Schedule schedule = scheduleRepository.findById(event.scheduleId())
                .orElseThrow();
        schedule.start();
        scheduleRepository.save(schedule);

        // Mettre a jour le bus
        Bus bus = busRepository.findById(event.busId()).orElseThrow();
        bus.startService();
        busRepository.save(bus);

        // Mettre a jour le conducteur
        Driver driver = driverRepository.findById(event.driverId()).orElseThrow();
        driver.startDuty();
        driverRepository.save(driver);
    }
}