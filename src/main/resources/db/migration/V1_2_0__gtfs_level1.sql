-- src/main/resources/db/migration/V1.2.0__gtfs_level1.sql

-- Ajout des coordonnees GPS sur les arrets existants
-- On met 0,0 par defaut — le joueur devra les renseigner
ALTER TABLE stops ADD COLUMN IF NOT EXISTS latitude  DOUBLE PRECISION NOT NULL DEFAULT 0.0;
ALTER TABLE stops ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION NOT NULL DEFAULT 0.0;

-- Ajout des champs GTFS sur les parcours
ALTER TABLE routes ADD COLUMN IF NOT EXISTS short_name  VARCHAR(10);
ALTER TABLE routes ADD COLUMN IF NOT EXISTS long_name   VARCHAR(255);
ALTER TABLE routes ADD COLUMN IF NOT EXISTS route_type  VARCHAR(20) NOT NULL DEFAULT 'BUS';

-- Initialiser long_name avec name pour les parcours existants
UPDATE routes SET long_name = name WHERE long_name IS NULL;
UPDATE routes SET short_name = name WHERE short_name IS NULL;