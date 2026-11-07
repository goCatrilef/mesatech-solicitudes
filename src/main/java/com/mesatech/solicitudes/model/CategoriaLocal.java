package com.mesatech.solicitudes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Copia local de una categoria del servicio Catalogo, recibida por evento.
 * La clave primaria ES el categoriaId original (no se autogenera) para conservar la referencia de origen.
 */
@Entity
@Table(name = "categoria_local")
public class CategoriaLocal {

    @Id
    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(nullable = false, length = 100)
    private String nombre;

    /** eventId del mensaje que origino esta copia (trazabilidad con el log del publicador). */
    @Column(name = "event_id", nullable = false, length = 64)
    private String eventId;

    /** fechaEvento enviada por Catalogo (ISO-8601, UTC). */
    @Column(name = "fecha_evento", nullable = false, length = 40)
    private String fechaEvento;

    @Column(name = "recibida_en", nullable = false)
    private Instant recibidaEn;

    public CategoriaLocal() {
    }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(String fechaEvento) { this.fechaEvento = fechaEvento; }

    public Instant getRecibidaEn() { return recibidaEn; }
    public void setRecibidaEn(Instant recibidaEn) { this.recibidaEn = recibidaEn; }
}
