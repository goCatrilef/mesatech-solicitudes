package com.mesatech.solicitudes.dto;

import com.mesatech.solicitudes.model.CategoriaLocal;
import com.mesatech.solicitudes.model.EstadoSolicitud;
import com.mesatech.solicitudes.model.PrioridadSolicitud;
import com.mesatech.solicitudes.model.Solicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class Dtos {

    private Dtos() {
    }

    /**
     * Contrato del evento consumido (mismo JSON que publica Catalogo).
     * fechaEvento: ISO-8601 en UTC.
     */
    public record EventoCategoriaMensaje(
            String eventId,
            String tipoEvento,
            String fechaEvento,
            Long categoriaId,
            String nombre
    ) {
    }

    /** El solicitante NO viene en el cuerpo: lo informa el BFF desde el JWT (cabecera X-Usuario). */
    public record SolicitudRequest(
            @NotBlank(message = "El titulo es obligatorio")
            @Size(max = 150, message = "El titulo admite hasta 150 caracteres")
            String titulo,

            @Size(max = 500, message = "La descripcion admite hasta 500 caracteres")
            String descripcion,

            @NotNull(message = "La categoria es obligatoria")
            @Positive(message = "El id de categoria debe ser mayor que cero")
            Long categoriaId,

            @NotNull(message = "La prioridad es obligatoria (BAJA, MEDIA, ALTA o CRITICA)")
            PrioridadSolicitud prioridad
    ) {
    }

    public record CambioEstadoRequest(
            @NotNull(message = "El estado es obligatorio")
            EstadoSolicitud estado,

            @Size(max = 500, message = "La atencion admite hasta 500 caracteres")
            String atencion
    ) {
    }

    public record CategoriaLocalResponse(Long categoriaId, String nombre, String eventId, String fechaEvento) {
        public static CategoriaLocalResponse de(CategoriaLocal c) {
            return new CategoriaLocalResponse(c.getCategoriaId(), c.getNombre(), c.getEventId(), c.getFechaEvento());
        }
    }

    /** v1: datos basicos de la solicitud. */
    public record SolicitudV1(Long id, String titulo, String descripcion, String solicitante,
                              Long categoriaId, PrioridadSolicitud prioridad, EstadoSolicitud estado) {
        public static SolicitudV1 de(Solicitud s) {
            return new SolicitudV1(s.getId(), s.getTitulo(), s.getDescripcion(), s.getSolicitante(),
                    s.getCategoriaId(), s.getPrioridad(), s.getEstado());
        }
    }

    /** v2: agrega nombre de categoria (desde la copia local), ultima atencion y fecha de creacion. */
    public record SolicitudV2(Long id, String titulo, String descripcion, String solicitante,
                              Long categoriaId, String categoriaNombre, PrioridadSolicitud prioridad,
                              EstadoSolicitud estado, String atencion, String fechaCreacion) {
        public static SolicitudV2 de(Solicitud s, String categoriaNombre) {
            return new SolicitudV2(s.getId(), s.getTitulo(), s.getDescripcion(), s.getSolicitante(),
                    s.getCategoriaId(), categoriaNombre, s.getPrioridad(), s.getEstado(),
                    s.getAtencion(), s.getFechaCreacion().toString());
        }
    }
}
