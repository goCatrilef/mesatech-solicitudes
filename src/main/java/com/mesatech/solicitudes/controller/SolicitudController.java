package com.mesatech.solicitudes.controller;

import com.mesatech.solicitudes.dto.Dtos.*;
import com.mesatech.solicitudes.service.CategoriaLocalService;
import com.mesatech.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Solicitudes detras del BFF (el BFF valida el JWT, autoriza por rol y envia el usuario en la cabecera X-Usuario).
 * Este servicio solo debe ser alcanzable desde el BFF (security group). v1 y v2 conviven.
 */
@RestController
@RequestMapping("/api")
public class SolicitudController {

    private static final String H_USUARIO = "X-Usuario";
    /** Sin BFF (pruebas directas con Postman) la cabecera es opcional. Con BFF siempre viene del JWT. */
    private static final String USUARIO_POR_DEFECTO = "usuario.demo@mesatech.cl";

    private final SolicitudService solicitudService;
    private final CategoriaLocalService categoriaLocalService;

    public SolicitudController(SolicitudService solicitudService, CategoriaLocalService categoriaLocalService) {
        this.solicitudService = solicitudService;
        this.categoriaLocalService = categoriaLocalService;
    }

    // ---- Categorias locales (copia recibida por RabbitMQ) ----

    @GetMapping("/v1/solicitudes/categorias")
    public List<CategoriaLocalResponse> listarCategoriasLocales() {
        return categoriaLocalService.listar();
    }

    /** 200 si la categoria ya llego; 404 si todavia no (la interfaz puede consultar hasta obtener 200). */
    @GetMapping("/v1/solicitudes/categorias/{categoriaId}")
    public CategoriaLocalResponse obtenerCategoriaLocal(@PathVariable Long categoriaId) {
        return categoriaLocalService.obtener(categoriaId);
    }

    // ---- v1 ----

    @PostMapping("/v1/solicitudes")
    public ResponseEntity<SolicitudV1> crearV1(@RequestHeader(value = H_USUARIO, defaultValue = USUARIO_POR_DEFECTO) String usuario,
                                               @Valid @RequestBody SolicitudRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SolicitudV1.de(solicitudService.crear(request, usuario)));
    }

    @GetMapping("/v1/solicitudes/mias")
    public List<SolicitudV1> misSolicitudesV1(@RequestHeader(value = H_USUARIO, defaultValue = USUARIO_POR_DEFECTO) String usuario) {
        return solicitudService.misSolicitudesV1(usuario);
    }

    @GetMapping("/v1/solicitudes")
    public List<SolicitudV1> listarV1() {
        return solicitudService.listarV1();
    }

    @GetMapping("/v1/solicitudes/{id}")
    public SolicitudV1 obtenerV1(@PathVariable Long id) {
        return SolicitudV1.de(solicitudService.obtener(id));
    }

    @PatchMapping("/v1/solicitudes/{id}/estado")
    public SolicitudV1 cambiarEstadoV1(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return SolicitudV1.de(solicitudService.cambiarEstado(id, request));
    }

    // ---- v2 (agrega categoriaNombre, atencion y fechaCreacion) ----

    @PostMapping("/v2/solicitudes")
    public ResponseEntity<SolicitudV2> crearV2(@RequestHeader(value = H_USUARIO, defaultValue = USUARIO_POR_DEFECTO) String usuario,
                                               @Valid @RequestBody SolicitudRequest request) {
        var creada = solicitudService.crear(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.obtenerV2(creada.getId()));
    }

    @GetMapping("/v2/solicitudes/mias")
    public List<SolicitudV2> misSolicitudesV2(@RequestHeader(value = H_USUARIO, defaultValue = USUARIO_POR_DEFECTO) String usuario) {
        return solicitudService.misSolicitudesV2(usuario);
    }

    @GetMapping("/v2/solicitudes")
    public List<SolicitudV2> listarV2() {
        return solicitudService.listarV2();
    }

    @GetMapping("/v2/solicitudes/{id}")
    public SolicitudV2 obtenerV2(@PathVariable Long id) {
        return solicitudService.obtenerV2(id);
    }

    @PatchMapping("/v2/solicitudes/{id}/estado")
    public SolicitudV2 cambiarEstadoV2(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        solicitudService.cambiarEstado(id, request);
        return solicitudService.obtenerV2(id);
    }
}
