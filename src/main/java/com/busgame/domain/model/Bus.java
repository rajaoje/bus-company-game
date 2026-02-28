// domain/model/Bus.java
package com.busgame.domain.model;

import java.util.UUID;

/**
 * Entité racine (Aggregate Root) du domaine Bus.
 * En DDD, l'Aggregate Root est le point d'entrée unique pour modifier
 * l'état d'un groupe d'objets cohérents. Ici, c'est le Bus lui-même.
 *
 * IMPORTANT : Cette classe ne dépend d'aucun framework.
 * Elle représente la vérité métier, indépendamment de comment elle est stockée.
 */
public class Bus {

    private final BusId id;
    private String model;
    private int capacity;
    private BusStatus status;
    private int mileage; // kilométrage, pertinent pour la maintenance dans le jeu
    private String busNumber;
    // Constructeur privé — on passe par la factory method pour garantir les invariants
    private Bus(BusId id, String model, int capacity) {
        this.id = id;
        this.model = model;
        this.capacity = capacity;
        this.status = BusStatus.AVAILABLE; // Un bus neuf est disponible par défaut
        this.mileage = 0;
    }

    /**
     * Factory method : point d'entrée unique pour créer un nouveau bus.
     * Cela nous permet de valider les règles métier AVANT que l'objet existe.
        busNumber est obligatoire et doit
     * etre valide avant meme d'arriver dans l'entite.
     */
    public static Bus register(String model, int capacity, String busNumber) {
        if (model == null || model.isBlank())
            throw new IllegalArgumentException("Le modele est obligatoire.");
        if (capacity <= 0)
            throw new IllegalArgumentException("La capacite doit etre positive.");
        if (busNumber == null || busNumber.isBlank())
            throw new IllegalArgumentException("Le numero de bus est obligatoire.");

        Bus bus = new Bus(new BusId(UUID.randomUUID()), model, capacity);
        bus.busNumber = busNumber.trim().toUpperCase();
        return bus;
    }

    /**
     * Méthode métier : envoyer un bus en maintenance.
     * Remarque : on encode une règle ici — on ne peut pas envoyer en maintenance
     * un bus déjà en maintenance ou retiré.
     */
    public void sendToMaintenance() {
        if (this.status == BusStatus.RETIRED) {
            throw new IllegalStateException("Impossible d'envoyer un bus retiré en maintenance.");
        }
        this.status = BusStatus.IN_MAINTENANCE;
    }

    public void returnFromMaintenance() {
        if (this.status != BusStatus.IN_MAINTENANCE) {
            throw new IllegalStateException("Ce bus n'est pas en maintenance.");
        }
        this.status = BusStatus.AVAILABLE;
    }

    public void retire() {
        this.status = BusStatus.RETIRED;
    }

    /**
     * Ajouter des kilomètres — appelé par le moteur de simulation
     * quand un bus termine un trajet.
     */
    public void addMileage(int kilometers) {
        if (kilometers < 0) throw new IllegalArgumentException("Les kilomètres ne peuvent pas être négatifs.");
        this.mileage += kilometers;
    }

    // Getters (pas de setters publics — on passe par les méthodes métier)
    public BusId getId() { return id; }
    public String getModel() { return model; }
    public int getCapacity() { return capacity; }
    public BusStatus getStatus() { return status; }
    public int getMileage() { return mileage; }
    public String getBusNumber() { return busNumber; }

    // À ajouter dans Bus.java

    /**
     * Méthode de reconstitution : utilisée uniquement pour recréer un Bus
     * depuis une source externe (BDD, fichier...). Contrairement à register(),
     * elle ne génère pas de nouvel ID et accepte tous les états.
     * Le nom "reconstitute" rend l'intention explicite.
     */
    public static Bus reconstitute(BusId id, String model, int capacity,
                                   BusStatus status, int mileage,
                                   String busNumber) {
        Bus bus = new Bus(id, model, capacity);
        bus.status    = status;
        bus.mileage   = mileage;
        bus.busNumber = busNumber;
        return bus;
    }

    /**
     * Prendre un service — le bus est maintenant en route.
     * Appele par le moteur de simulation quand un horaire demarre.
     */
    public void startService() {
        if (this.status != BusStatus.AVAILABLE)
            throw new IllegalStateException(
                    "Le bus doit etre AVAILABLE pour demarrer un service.");
        this.status = BusStatus.IN_SERVICE;
    }

    /**
     * Terminer un service — le bus retourne au depot.
     */
    public void endService() {
        if (this.status != BusStatus.IN_SERVICE)
            throw new IllegalStateException(
                    "Le bus doit etre IN_SERVICE pour terminer un service.");
        this.status = BusStatus.AVAILABLE;
    }
}