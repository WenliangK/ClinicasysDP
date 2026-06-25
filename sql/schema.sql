-- Ejecutar en Fedora:
--   sudo -u postgres psql
--   CREATE DATABASE clinicasys_db;
--   \c clinicasys_db
--   \i ruta/a/schema.sql

CREATE TABLE IF NOT EXISTS pacientes (
    id        SERIAL PRIMARY KEY,
    nombre    VARCHAR(120) NOT NULL,
    dni       VARCHAR(8)   NOT NULL UNIQUE,
    telefono  VARCHAR(9)   NOT NULL,
    email     VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS medicos (
    id            SERIAL PRIMARY KEY,
    nombre        VARCHAR(120) NOT NULL,
    especialidad  VARCHAR(100) NOT NULL,
    tipo          VARCHAR(20)  NOT NULL CHECK (tipo IN ('PRIVADO', 'PUBLICO'))
);

CREATE TABLE IF NOT EXISTS citas (
    id           SERIAL PRIMARY KEY,
    paciente_id  INTEGER NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    medico       VARCHAR(120) NOT NULL,
    fecha_hora   TIMESTAMP NOT NULL,
    motivo       VARCHAR(255) NOT NULL,
    estado       VARCHAR(20)  NOT NULL DEFAULT 'EN_ESPERA'
                 CHECK (estado IN ('EN_ESPERA', 'EN_CONSULTORIO', 'ATENDIDO', 'CANCELADO'))
);

CREATE TABLE IF NOT EXISTS facturas (
    id             SERIAL PRIMARY KEY,
    cita_id        INTEGER REFERENCES citas(id) ON DELETE SET NULL,
    descripcion    TEXT NOT NULL,
    costo          NUMERIC(8,2) NOT NULL,
    fecha_emision  TIMESTAMP NOT NULL DEFAULT NOW()
);
