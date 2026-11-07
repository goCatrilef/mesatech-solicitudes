package com.mesatech.solicitudes.service;

import com.mesatech.solicitudes.dto.Dtos.CategoriaLocalResponse;
import com.mesatech.solicitudes.dto.Dtos.EventoCategoriaMensaje;
import com.mesatech.solicitudes.model.CategoriaLocal;
import com.mesatech.solicitudes.repository.CategoriaLocalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class CategoriaLocalService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaLocalService.class);
    private static final String TIPO_CREADA = "CATEGORIA_CREADA";

    private final CategoriaLocalRepository repository;

    public CategoriaLocalService(CategoriaLocalRepository repository) {
        this.repository = repository;
    }

    /**
     * Guarda la copia local. La PK es el categoriaId original, asi que un mensaje re-entregado
     * actualiza la misma fila en vez de duplicarla (no es una garantia de "exactamente una vez").
     * Un mensaje invalido se rechaza SIN reencolar (si no, quedaria reintentandose para siempre).
     */
    @Transactional
    public void guardar(EventoCategoriaMensaje evento) {
        if (evento.categoriaId() == null || evento.nombre() == null || evento.nombre().isBlank()
                || evento.eventId() == null || evento.fechaEvento() == null) {
            throw new AmqpRejectAndDontRequeueException("Evento invalido (faltan campos): " + evento);
        }
        if (!TIPO_CREADA.equals(evento.tipoEvento())) {
            log.warn("Evento ignorado, tipoEvento no soportado: {} (eventId={})", evento.tipoEvento(), evento.eventId());
            return;
        }

        CategoriaLocal copia = new CategoriaLocal();
        copia.setCategoriaId(evento.categoriaId());
        copia.setNombre(evento.nombre());
        copia.setEventId(evento.eventId());
        copia.setFechaEvento(evento.fechaEvento());
        copia.setRecibidaEn(Instant.now());
        repository.save(copia);
    }

    @Transactional(readOnly = true)
    public List<CategoriaLocalResponse> listar() {
        return repository.findAll().stream().map(CategoriaLocalResponse::de).toList();
    }

    /** 404 mientras la categoria aun no llego por evento: sirve para que la interfaz sepa cuando ya esta disponible. */
    @Transactional(readOnly = true)
    public CategoriaLocalResponse obtener(Long categoriaId) {
        return repository.findById(categoriaId)
                .map(CategoriaLocalResponse::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "La categoria " + categoriaId + " aun no esta disponible en Solicitudes"));
    }
}
