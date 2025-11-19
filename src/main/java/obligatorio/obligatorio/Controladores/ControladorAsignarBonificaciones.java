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
    private Propietario propietarioActual;

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

        List<Bonificacion> bonificaciones = fachada.getBonificacionesDefinidas();
        List<Map<String, String>> bonisDTO = bonificaciones.stream()
                .map(b -> Map.of("nombre", b.getNombre()))
                .collect(Collectors.toList());

        List<PuestoDTO> puestos = fachada.getPuestosDTO();

        return Respuesta.lista(
                new Respuesta("bonificaciones", bonisDTO),
                new Respuesta("puestos", puestos));
    }

    @PostMapping("/vistaCerrada")
    public void vistaCerrada() {
        if (propietarioActual != null) {
            propietarioActual.quitarObservador(this);
            propietarioActual = null;
        }
    }

    @GetMapping("/puestos")
    public List<PuestoDTO> obtenerPuestos() {
        return fachada.getPuestosDTO();
    }

    @PostMapping("/buscarPropietario")
    public Object buscarPropietario(
            @SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin,
            @RequestParam String cedula) {

        if (propietarioActual != null) {
            propietarioActual.quitarObservador(this);
            propietarioActual = null;
        }

        Propietario p = fachada.getPropietarioPorCedula(cedula);
        if (p == null) {
            return Respuesta.lista(
                    new Respuesta("resultadoBusqueda", null));
        }

        propietarioActual = p;
        propietarioActual.agregarObservador(this);

        PropietarioBonificacionesDTO dto = construirDTO(p);

        return Respuesta.lista(
                new Respuesta("resultadoBusqueda", dto));
    }

    @PostMapping("/asignar")
    public Object asignarBonificacion(
            @SessionAttribute(name = "usuarioAdmin") obligatorio.obligatorio.Modelo.modelos.Administrador admin,
            @RequestParam String cedula,
            @RequestParam String bonificacion,
            @RequestParam String puesto) {

        Propietario p = fachada.getPropietarioPorCedula(cedula);
        if (p == null) {
            return Respuesta.lista(
                    new Respuesta("errorAsignacion", "No existe el propietario"));
        }

        if (propietarioActual == null || !propietarioActual.equals(p)) {
            if (propietarioActual != null) {
                propietarioActual.quitarObservador(this);
            }
            propietarioActual = p;
            propietarioActual.agregarObservador(this);
        }

        Bonificacion bon = fachada.getBonificacionesDefinidas().stream()
                .filter(b -> b.getNombre().equals(bonificacion))
                .findFirst()
                .orElse(null);

        Puesto pst = fachada.getPuestos().stream()
                .filter(pt -> pt.getNombre().equals(puesto))
                .findFirst()
                .orElse(null);

        if (bon == null) {
            return Respuesta.lista(
                    new Respuesta("errorAsignacion", "Debe especificar una bonificación"));
        }
        if (pst == null) {
            return Respuesta.lista(
                    new Respuesta("errorAsignacion", "Debe especificar un puesto"));
        }

        try {
            p.asignarBonificacion(bon, pst);
        } catch (ObligatorioException e) {
            return Respuesta.lista(
                    new Respuesta("errorAsignacion", e.getMessage()));
        }

        PropietarioBonificacionesDTO dto = construirDTO(p);
        return Respuesta.lista(
                new Respuesta("resultadoBusqueda", dto));
    }

    private PropietarioBonificacionesDTO construirDTO(Propietario p) {
        String estado = p.getEstadoPropietario() != null
                ? p.getEstadoPropietario().getNombre()
                : "";

        List<BonificacionAsignadaDTO> bonificacionesAsignadas = p.getAsignaciones()
                .stream()
                .map(a -> new BonificacionAsignadaDTO(
                        a.getBonificacion().getNombre(),
                        a.getPuesto().getNombre(),
                        a.getFechaAsignacion()))
                .toList();

        return new PropietarioBonificacionesDTO(
                p.getNombreCompleto(),
                estado,
                bonificacionesAsignadas);
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        if (!(origen instanceof Propietario p)) {
            return;
        }
        if (propietarioActual == null || !propietarioActual.equals(p)) {
            return;
        }
        if (conexionNavegador.getConexionSSE() == null) {
            return;
        }

        if (evento == Propietario.Eventos.BONIFICACION_ASIGNADA
                || evento == Propietario.Eventos.CAMBIO_ESTADO) {

            PropietarioBonificacionesDTO dto = construirDTO(p);
            conexionNavegador.enviarJSON(
                    Respuesta.lista(
                            new Respuesta("resultadoBusqueda", dto)));
        }
    }
}
