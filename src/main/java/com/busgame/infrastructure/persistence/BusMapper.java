// infrastructure/persistence/BusMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Bus;
import com.busgame.domain.model.BusId;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Mapper : traduit entre entités JPA et entités domaine.
 *
 * Note sur la réflexion : notre entité domaine Bus a un constructeur privé
 * pour protéger ses invariants. Pour reconstruire un Bus depuis la BDD
 * (il existe déjà, il a déjà été validé), on utilise la réflexion.
 * Une alternative serait d'avoir un constructeur de "reconstitution"
 * package-private, ou d'utiliser une librairie comme MapStruct.
 * Pour la lisibilité pédagogique, voici la version explicite.
 */
@Component
public class BusMapper {

    public BusJpaEntity toEntity(Bus bus) {
        return new BusJpaEntity(
                bus.getId().value(),
                bus.getModel(),
                bus.getCapacity(),
                bus.getStatus(),
                bus.getMileage(),
                bus.getBusNumber()
        );
    }

    /**
     * Reconstitue un Bus depuis une entité JPA.
     * On utilise un constructeur de reconstitution que l'on va ajouter à Bus.
     */
    public Bus toDomain(BusJpaEntity entity) {
        return Bus.reconstitute(
                new BusId(entity.getId()),
                entity.getModel(),
                entity.getCapacity(),
                entity.getStatus(),
                entity.getMileage(),
                entity.getBusNumber()
        );
    }
}