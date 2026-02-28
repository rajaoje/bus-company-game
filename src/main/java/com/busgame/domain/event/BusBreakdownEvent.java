// domain/event/BusBreakdownEvent.java
package com.busgame.domain.event;

import com.busgame.domain.model.BusId;
import com.busgame.domain.model.ScheduleId;
import java.time.LocalDateTime;

/**
 * Evenement publie quand une panne aleatoire survient.
 * Le handler va annuler l'horaire en cours et envoyer le bus en maintenance.
 */
public record BusBreakdownEvent(
        BusId busId,
        ScheduleId affectedScheduleId,
        LocalDateTime gameTime
) {}