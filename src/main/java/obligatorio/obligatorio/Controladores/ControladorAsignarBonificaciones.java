package obligatorio.obligatorio.Controladores;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.Bonificacion;
import obligatorio.obligatorio.Modelo.modelos.ConexionNavegador;
import obligatorio.obligatorio.observador.Observable;
import obligatorio.obligatorio.observador.Observador;


@RestController
@RequestMapping("/asignarBonificaciones")
@Scope("session")
public class ControladorAsignarBonificaciones implements Observador {
    private final Fachada fachada = Fachada.getInstancia();
    private final ConexionNavegador conexionNavegador;

    public ControladorAsignarBonificaciones(@Autowired ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE(@SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin) {
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    @PostMapping("/vistaConectada")
    public Object vistaConectada(@SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin) {
        // Registrar como observador (única vez por sesión)
        fachada.agregarObservador(this);
        List<Bonificacion> bonificaciones = fachada.getBonificacionesDefinidas();
        return bonificaciones.stream()
                .map(b -> Map.of("nombre", b.getNombre()))
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<Map<String, String>> obtenerBonificaciones() {
        List<Bonificacion> bonificaciones = fachada.getBonificacionesDefinidas();
        return bonificaciones.stream()
                .map(b -> Map.of("nombre", b.getNombre()))
                .collect(Collectors.toList());
    }

    @PostMapping("/vistaCerrada")
    public void vistaCerrada() {
        fachada.quitarObservador(this);
    }
    
    @GetMapping("/puestos")
    public List<PuestoDTO> obtenerPuestos() {
        return fachada.getPuestosDTO();
    }



    @PostMapping("/buscarPropietario")
    public Respuesta buscarPropietario(@SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin,
                                       @org.springframework.web.bind.annotation.RequestParam String cedula) {
        obligatorio.obligatorio.Modelo.modelos.Propietario p = fachada.getPropietarioPorCedula(cedula);
        if (p == null) {
            return new Respuesta("resultadoBusqueda", null);
        }
        String estado = p.getEstadoPropietario() != null ? p.getEstadoPropietario().getNombre() : "";
        java.util.List<obligatorio.obligatorio.DTO.BonificacionAsignadaDTO> bonificacionesAsignadas = new java.util.ArrayList<>();
        for (obligatorio.obligatorio.Modelo.modelos.AsignacionBonificacion asignacion : p.getAsignaciones()) {
            String nombreBonificacion = asignacion.getBonificacion().getNombre();
            String nombrePuesto = asignacion.getPuesto().getNombre();
            bonificacionesAsignadas.add(new obligatorio.obligatorio.DTO.BonificacionAsignadaDTO(nombreBonificacion, nombrePuesto, asignacion.getFechaAsignacion()));
        }
        obligatorio.obligatorio.DTO.PropietarioBonificacionesDTO dto = new obligatorio.obligatorio.DTO.PropietarioBonificacionesDTO(
            p.getNombreCompleto(),
            estado,
            bonificacionesAsignadas
        );
        return new Respuesta("resultadoBusqueda", dto);
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        // Aquí puedes enviar actualizaciones SSE si lo necesitas
        // Ejemplo: enviar la lista actualizada de bonificaciones
        if (conexionNavegador.getConexionSSE() != null) {
            conexionNavegador.enviarJSON(fachada.getBonificacionesDefinidas());
        }
    }
}
