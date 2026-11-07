package com.mesatech.solicitudes.service;

import com.mesatech.solicitudes.dto.Dtos.CambioEstadoRequest;
import com.mesatech.solicitudes.dto.Dtos.SolicitudRequest;
import com.mesatech.solicitudes.dto.Dtos.SolicitudV1;
import com.mesatech.solicitudes.dto.Dtos.SolicitudV2;
import com.mesatech.solicitudes.model.CategoriaLocal;
import com.mesatech.solicitudes.model.EstadoSolicitud;
import com.mesatech.solicitudes.model.Solicitud;
import com.mesatech.solicitudes.repository.CategoriaLocalRepository;
import com.mesatech.solicitudes.repository.SolicitudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final CategoriaLocalRepository categoriaLocalRepository;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            CategoriaLocalRepository categoriaLocalRepository) {
        this.solicitudRepository = solicitudRepository;
        this.categoriaLocalRepository = categoriaLocalRepository;
    }

    /** Solo acepta categorias presentes en la COPIA LOCAL (nunca consulta el RDS de Catalogo). */
    @Transactional
    public Solicitud crear(SolicitudRequest request, String usuario) {
        if (!categoriaLocalRepository.existsById(request.categoriaId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La categoria " + request.categoriaId() + " aun no esta disponible en Solicitudes "
                            + "(el procesamiento es asincrono). Reintenta en unos segundos.");
        }
        Solicitud s = new Solicitud();
        s.setTitulo(request.titulo().trim());
        s.setDescripcion(request.descripcion());
        s.setSolicitante(usuario);
        s.setCategoriaId(request.categoriaId());
        s.setPrioridad(request.prioridad());
        s.setEstado(EstadoSolicitud.CREADA);
        s.setFechaCreacion(Instant.now());
        return solicitudRepository.save(s);
    }

    @Transactional
    public Solicitud cambiarEstado(Long id, CambioEstadoRequest request) {
        Solicitud s = obtener(id);
        EstadoSolicitud nuevo = request.estado();
        if (!s.getEstado().puedePasarA(nuevo)) {
            String motivo = nuevo == EstadoSolicitud.RESUELTA
                    ? " (una solicitud solo puede resolverse si esta EN_PROCESO)"
                    : " (flujo: CREADA -> ASIGNADA -> EN_PROCESO -> RESUELTA -> CERRADA; CANCELADA cuando corresponda)";
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transicion no permitida: " + s.getEstado() + " -> " + nuevo + motivo);
        }
        s.setEstado(nuevo);
        if (request.atencion() != null && !request.atencion().isBlank()) {
            s.setAtencion(request.atencion());
        }
        return solicitudRepository.save(s);
    }

    @Transactional(readOnly = true)
    public Solicitud obtener(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud " + id + " no encontrada"));
    }

    // ---- v1 ----

    @Transactional(readOnly = true)
    public List<SolicitudV1> listarV1() {
        return solicitudRepository.findAll().stream().map(SolicitudV1::de).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitudV1> misSolicitudesV1(String usuario) {
        return solicitudRepository.findBySolicitanteOrderByFechaCreacionDesc(usuario)
                .stream().map(SolicitudV1::de).toList();
    }

    // ---- v2 ----

    @Transactional(readOnly = true)
    public List<SolicitudV2> listarV2() {
        return aV2(solicitudRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<SolicitudV2> misSolicitudesV2(String usuario) {
        return aV2(solicitudRepository.findBySolicitanteOrderByFechaCreacionDesc(usuario));
    }

    @Transactional(readOnly = true)
    public SolicitudV2 obtenerV2(Long id) {
        Solicitud s = obtener(id);
        String nombre = categoriaLocalRepository.findById(s.getCategoriaId())
                .map(CategoriaLocal::getNombre).orElse(null);
        return SolicitudV2.de(s, nombre);
    }

    private List<SolicitudV2> aV2(List<Solicitud> solicitudes) {
        Map<Long, String> nombres = categoriaLocalRepository.findAll().stream()
                .collect(Collectors.toMap(CategoriaLocal::getCategoriaId, CategoriaLocal::getNombre, (a, b) -> a));
        return solicitudes.stream()
                .map(s -> SolicitudV2.de(s, nombres.get(s.getCategoriaId())))
                .toList();
    }
}
