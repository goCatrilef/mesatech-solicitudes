package com.mesatech.solicitudes.model;

/**
 * Flujo EP1: CREADA -> ASIGNADA -> EN_PROCESO -> RESUELTA -> CERRADA, y CANCELADA cuando corresponda.
 * Regla de negocio minima: no se puede pasar a RESUELTA si antes no esta EN_PROCESO.
 */
public enum EstadoSolicitud {
    CREADA,
    ASIGNADA,
    EN_PROCESO,
    RESUELTA,
    CERRADA,
    CANCELADA;

    public boolean puedePasarA(EstadoSolicitud destino) {
        return switch (this) {
            case CREADA -> destino == ASIGNADA || destino == CANCELADA;
            case ASIGNADA -> destino == EN_PROCESO || destino == CANCELADA;
            case EN_PROCESO -> destino == RESUELTA || destino == CANCELADA;
            case RESUELTA -> destino == CERRADA;
            case CERRADA, CANCELADA -> false;
        };
    }
}
