package com.mesatech.solicitudes.listener;

import com.mesatech.solicitudes.dto.Dtos.EventoCategoriaMensaje;
import com.mesatech.solicitudes.service.CategoriaLocalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de la cola solicitudes.catalogo.queue.
 * Con acknowledge-mode=auto, Spring confirma (ack) el mensaje SOLO si este metodo termina sin excepcion,
 * o sea despues de guardar en el RDS de Solicitudes. Si lanza una excepcion, el mensaje se reencola.
 */
@Component
public class CategoriaListener {

    private static final Logger log = LoggerFactory.getLogger(CategoriaListener.class);

    private final CategoriaLocalService categoriaLocalService;

    public CategoriaListener(CategoriaLocalService categoriaLocalService) {
        this.categoriaLocalService = categoriaLocalService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumir(EventoCategoriaMensaje evento) {
        log.info("Evento recibido: eventId={} tipoEvento={} categoriaId={} nombre={}",
                evento.eventId(), evento.tipoEvento(), evento.categoriaId(), evento.nombre());

        categoriaLocalService.guardar(evento);   // si falla, la excepcion sube y no hay ack

        log.info("Categoria guardada en RDS Solicitudes: categoriaId={} eventId={}",
                evento.categoriaId(), evento.eventId());
    }
}
