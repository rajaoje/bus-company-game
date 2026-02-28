package com.busgame.domain.model;

/**
     * Type de transport GTFS.
     * On se limite a BUS pour l'instant mais la structure
     * permet d'etendre vers tram, metro, etc.
     */
    public enum RouteType {
        TRAM(0), SUBWAY(1), RAIL(2), BUS(3), FERRY(4);

        private final int gtfsCode;

        RouteType(int gtfsCode) { this.gtfsCode = gtfsCode; }
        public int getGtfsCode() { return gtfsCode; }
    }
