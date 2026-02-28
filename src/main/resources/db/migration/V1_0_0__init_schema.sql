CREATE TABLE IF NOT EXISTS buses (
                                     id          UUID PRIMARY KEY,
                                     model       VARCHAR(255) NOT NULL,
    capacity    INTEGER NOT NULL,
    status      VARCHAR(50) NOT NULL,
    mileage     INTEGER NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS drivers (
                                       id                   UUID PRIMARY KEY,
                                       first_name           VARCHAR(255) NOT NULL,
    last_name            VARCHAR(255) NOT NULL,
    email                VARCHAR(255) NOT NULL UNIQUE,
    status               VARCHAR(50) NOT NULL,
    weekly_hours_worked  DOUBLE PRECISION NOT NULL DEFAULT 0,
    max_weekly_hours     INTEGER NOT NULL DEFAULT 35
    );

CREATE TABLE IF NOT EXISTS routes (
                                      id          UUID PRIMARY KEY,
                                      name        VARCHAR(255) NOT NULL,
    description VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS stops (
                                     id                        UUID PRIMARY KEY,
                                     route_id                  UUID NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    name                      VARCHAR(255) NOT NULL,
    sequence_order            INTEGER NOT NULL,
    distance_from_previous_km DOUBLE PRECISION NOT NULL
    );

CREATE TABLE IF NOT EXISTS schedules (
                                         id           UUID PRIMARY KEY,
                                         bus_id       UUID NOT NULL REFERENCES buses(id),
    driver_id    UUID NOT NULL REFERENCES drivers(id),
    route_id     UUID NOT NULL REFERENCES routes(id),
    start_time   TIMESTAMP NOT NULL,
    end_time     TIMESTAMP NOT NULL,
    status       VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS maintenance_records (
                                                   id                  UUID PRIMARY KEY,
                                                   bus_id              UUID NOT NULL REFERENCES buses(id),
    cause               VARCHAR(50) NOT NULL,
    status              VARCHAR(50) NOT NULL,
    start_time          TIMESTAMP NOT NULL,
    scheduled_end_time  TIMESTAMP NOT NULL,
    actual_end_time     TIMESTAMP
    );