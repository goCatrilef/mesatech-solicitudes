-- RDS Solicitudes (instancia exclusiva, distinta a la de Catalogo).
-- Alternativa: dejar spring.jpa.hibernate.ddl-auto=update y que Hibernate cree las tablas.

CREATE DATABASE IF NOT EXISTS mesatech_solicitudes
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usuario de aplicacion (no usar el usuario administrador de RDS en la aplicacion)
-- CREATE USER 'solicitudes_app'@'%' IDENTIFIED BY '<clave-real>';
-- GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON mesatech_solicitudes.* TO 'solicitudes_app'@'%';

USE mesatech_solicitudes;

-- Copia local de categorias recibidas por RabbitMQ. categoria_id = id original de Catalogo (no autogenerado).
CREATE TABLE IF NOT EXISTS categoria_local (
  categoria_id  BIGINT        NOT NULL,
  nombre        VARCHAR(100)  NOT NULL,
  event_id      VARCHAR(64)   NOT NULL,
  fecha_evento  VARCHAR(40)   NOT NULL,
  recibida_en   DATETIME(6)   NOT NULL,
  PRIMARY KEY (categoria_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS solicitud (
  id              BIGINT        NOT NULL AUTO_INCREMENT,
  titulo          VARCHAR(150)  NOT NULL,
  descripcion     VARCHAR(500)  NULL,
  solicitante     VARCHAR(150)  NOT NULL,
  categoria_id    BIGINT        NOT NULL,
  prioridad       VARCHAR(20)   NOT NULL,
  estado          VARCHAR(20)   NOT NULL,
  atencion        VARCHAR(500)  NULL,
  fecha_creacion  DATETIME(6)   NOT NULL,
  PRIMARY KEY (id),
  KEY idx_solicitud_categoria (categoria_id),
  KEY idx_solicitud_solicitante (solicitante)
) ENGINE=InnoDB;
