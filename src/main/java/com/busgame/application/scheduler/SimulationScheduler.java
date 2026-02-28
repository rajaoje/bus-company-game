// application/scheduler/SimulationScheduler.java
package com.busgame.application.scheduler;

import com.busgame.domain.port.in.SimulationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Ce composant Spring appelle processTick() automatiquement
 * a intervalle regulier en temps reel.
 *
 * @Scheduled(fixedDelay = 5000) signifie : attendre 5 secondes
 * apres la fin du tick precedent avant de lancer le suivant.
 * On utilise fixedDelay et non fixedRate pour eviter les chevauchements
 * si un tick prend plus de temps que prevu.
 *
 * Pour activer le scheduling, il faut ajouter @EnableScheduling
 * sur la classe principale de l'application.
 */
@Component
public class SimulationScheduler {

    private static final Logger log = LoggerFactory.getLogger(SimulationScheduler.class);
    private final SimulationUseCase simulationUseCase;

    public SimulationScheduler(SimulationUseCase simulationUseCase) {
        this.simulationUseCase = simulationUseCase;
    }

    @Scheduled(fixedDelay = 5000) // Toutes les 5 secondes en temps reel
    public void tick() {
        try {
            simulationUseCase.processTick();
        } catch (Exception e) {
            // On logue sans laisser l'exception remonter —
            // une erreur de tick ne doit pas stopper le scheduler
            log.error("Erreur lors du tick de simulation : {}", e.getMessage());
        }
    }
}