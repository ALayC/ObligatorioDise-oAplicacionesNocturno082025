
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import obligatorio.obligatorio.DTO.BonificacionAsignadaDTO;
import obligatorio.obligatorio.DTO.PropietarioBonificacionesDTO;
import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.AsignacionBonificacion;
import obligatorio.obligatorio.Modelo.modelos.Bonificacion;
import obligatorio.obligatorio.Modelo.modelos.ConexionNavegador;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Puesto;
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
    public SseEmitter registrarSSE(
            @SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin) {
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    @PostMapping("/vistaConectada")
    public Object vistaConectada(
            @SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin) {
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
    public Respuesta buscarPropietario(
            @SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin,
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
            bonificacionesAsignadas.add(new obligatorio.obligatorio.DTO.BonificacionAsignadaDTO(nombreBonificacion,
                    nombrePuesto, asignacion.getFechaAsignacion()));
        }
        obligatorio.obligatorio.DTO.PropietarioBonificacionesDTO dto = new obligatorio.obligatorio.DTO.PropietarioBonificacionesDTO(
                p.getNombreCompleto(),
                estado,
                bonificacionesAsignadas);
        return new Respuesta("resultadoBusqueda", dto);
    }

    @PostMapping("/asignar")
    public Respuesta asignarBonificacion(
            @SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin,
            @RequestParam String cedula,
            @RequestParam String bonificacion,
            @RequestParam String puesto) {

        // 1) Buscar propietario
        Propietario p = fachada.getPropietarioPorCedula(cedula);
        if (p == null) {
            // Podés discutir si querés "resultadoBusqueda = null" o un error específico.
            return new Respuesta("errorAsignacion", "No existe el propietario");
        }

        // 2) Resolver Bonificacion y Puesto a partir de sus nombres
        Bonificacion bon = fachada.getBonificacionesDefinidas().stream()
                .filter(b -> b.getNombre().equals(bonificacion))
                .findFirst()
                .orElse(null);

        Puesto pst = fachada.getPuestos().stream()
                .filter(pt -> pt.getNombre().equals(puesto))
                .findFirst()
                .orElse(null);

        // 3) Validaciones de selección (estas sí son de capa de aplicación / UI)
        if (bon == null) {
            return new Respuesta("errorAsignacion", "Debe especificar una bonificación");
        }
        if (pst == null) {
            return new Respuesta("errorAsignacion", "Debe especificar un puesto");
        }

        // 4) Delegar TODO el resto al dominio
        try {
            p.asignarBonificacion(bon, pst); // acá se aplican: estado, “ya tiene para el puesto”, fecha, evento, etc.
        } catch (ObligatorioException e) {
            return new Respuesta("errorAsignacion", e.getMessage());
        }

        // 5) Armar DTO de salida igual que antes
        String estado = p.getEstadoPropietario() != null ? p.getEstadoPropietario().getNombre() : "";
        List<BonificacionAsignadaDTO> bonificacionesAsignadas = new java.util.ArrayList<>();
        for (AsignacionBonificacion asignacion : p.getAsignaciones()) {
            String nombreBonificacion = asignacion.getBonificacion().getNombre();
            String nombrePuesto = asignacion.getPuesto().getNombre();
            bonificacionesAsignadas.add(
                    new BonificacionAsignadaDTO(nombreBonificacion, nombrePuesto, asignacion.getFechaAsignacion()));
        }
        PropietarioBonificacionesDTO dto = new PropietarioBonificacionesDTO(
                p.getNombreCompleto(),
                estado,
                bonificacionesAsignadas);

        return new Respuesta("resultadoBusqueda", dto);
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        // Enviar el tablero completo del propietario afectado
        // if (origen instanceof Propietario propietario &&
        // conexionNavegador.getConexionSSE() != null) {
        // conexionNavegador.enviarJSON(fachada.armarRespuestasTablero(propietario));
        // }
    }
}
