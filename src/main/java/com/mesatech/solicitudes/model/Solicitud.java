package com.mesatech.solicitudes.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    /** Usuario autenticado que crea la solicitud (lo informa el BFF desde el JWT en la cabecera X-Usuario). */
    @Column(nullable = false, length = 150)
    private String solicitante;

    /** Referencia a categoria_local.categoria_id (copia local; no se consulta el RDS de Catalogo). */
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadSolicitud prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado = EstadoSolicitud.CREADA;

    /** Ultima atencion registrada por el operador al cambiar el estado. */
    @Column(length = 500)
    private String atencion;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    public Solicitud() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public PrioridadSolicitud getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadSolicitud prioridad) { this.prioridad = prioridad; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public String getAtencion() { return atencion; }
    public void setAtencion(String atencion) { this.atencion = atencion; }

    public Instant getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Instant fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
