-- V1_4_0__gtfs_level3.sql

-- Champs GTFS dans routes (si pas encore presents)
ALTER TABLE routes ADD COLUMN IF NOT EXISTS
    short_name VARCHAR(20);
ALTER TABLE routes ADD COLUMN IF NOT EXISTS
    long_name  VARCHAR(255);
ALTER TABLE routes ADD COLUMN IF NOT EXISTS
    route_type VARCHAR(20) NOT NULL DEFAULT 'BUS';

-- Table calendars
CREATE TABLE calendars (
                           id          UUID    PRIMARY KEY,
                           monday      BOOLEAN NOT NULL DEFAULT FALSE,
                           tuesday     BOOLEAN NOT NULL DEFAULT FALSE,
                           wednesday   BOOLEAN NOT NULL DEFAULT FALSE,
                           thursday    BOOLEAN NOT NULL DEFAULT FALSE,
                           friday      BOOLEAN NOT NULL DEFAULT FALSE,
                           saturday    BOOLEAN NOT NULL DEFAULT FALSE,
                           sunday      BOOLEAN NOT NULL DEFAULT FALSE,
                           start_date  DATE    NOT NULL,
                           end_date    DATE    NOT NULL
);

-- Table calendar_dates
CREATE TABLE calendar_dates (
                                id              UUID        PRIMARY KEY,
                                service_id      UUID        NOT NULL REFERENCES calendars(id),
                                date            DATE        NOT NULL,
                                exception_type  VARCHAR(10) NOT NULL,
                                UNIQUE (service_id, date)
);

-- service_id dans trips
ALTER TABLE trips ADD COLUMN IF NOT EXISTS
    service_id UUID REFERENCES calendars(id);