-- V1_3_0__gtfs_level2.sql

-- Stops devient une table independante
-- On supprime les colonnes qui appartenaient a l'ancienne relation Route → Stop
ALTER TABLE stops DROP COLUMN IF EXISTS route_id;
ALTER TABLE stops DROP COLUMN IF EXISTS sequence_order;
ALTER TABLE stops DROP COLUMN IF EXISTS distance_from_previous_km;

-- Nouvelle table trips
CREATE TABLE trips (
                       id           UUID PRIMARY KEY,
                       route_id     UUID NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
                       direction_id VARCHAR(20) NOT NULL DEFAULT 'OUTBOUND',
                       headsign     VARCHAR(255) NOT NULL
);

-- Nouvelle table stop_times — le lien entre Trip et Stop
CREATE TABLE stop_times (
                            id                        UUID PRIMARY KEY,
                            trip_id                   UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
                            stop_id                   UUID NOT NULL REFERENCES stops(id),
                            stop_sequence             INTEGER NOT NULL,
                            arrival_time              TIME NOT NULL,
                            departure_time            TIME NOT NULL,
                            distance_from_previous_km DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                            UNIQUE (trip_id, stop_sequence)
);

-- Mettre a jour schedules pour referencer trip_id au lieu de route_id
ALTER TABLE schedules ADD COLUMN trip_id UUID REFERENCES trips(id);
-- On garde route_id pour la retrocompatibilite le temps de la migration
-- Il sera supprime dans V1_4_0 une fois le front-end mis a jour