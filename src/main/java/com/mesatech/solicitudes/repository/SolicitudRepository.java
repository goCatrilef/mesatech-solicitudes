package com.mesatech.solicitudes.repository;

import com.mesatech.solicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findBySolicitanteOrderByFechaCreacionDesc(String solicitante);
}
