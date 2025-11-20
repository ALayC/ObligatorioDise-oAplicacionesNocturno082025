package obligatorio.obligatorio.Controladores;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import obligatorio.obligatorio.DTO.BonificacionAsignadaDTO;
import obligatorio.obligatorio.DTO.CabeceraPropietarioDTO;
import obligatorio.obligatorio.DTO.NotificacionDTO;
import obligatorio.obligatorio.DTO.TransitoDTO;
import obligatorio.obligatorio.DTO.VehiculoResumenDTO;
import obligatorio.obligatorio.Modelo.modelos.AsignacionBonificacion;
import obligatorio.obligatorio.Modelo.modelos.ConexionNavegador;
import obligatorio.obligatorio.Modelo.modelos.Notificacion;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Sesion;
import obligatorio.obligatorio.Modelo.modelos.Transito;
import obligatorio.obligatorio.Modelo.modelos.Vehiculo;
import obligatorio.obligatorio.observador.Observable;
import obligatorio.obligatorio.observador.Observador;

@RestController
@RequestMapping("/propietario")
@Scope("session")
public class CasoUsoTableroPropietario implements Observador {
    private Propietario propietario;
    private final ConexionNavegador conexionNavegador;

    public CasoUsoTableroPropietario(@Autowired ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE(
            @SessionAttribute(name = "usuarioPropietario") Sesion sesion) {
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    @PostMapping("/vistaConectada")
    public List<Respuesta> inicializarVista(@SessionAttribute(name = "usuarioPropietario") Sesion sesion) {
        propietario = sesion.getPropietario();
        System.out.println("[LOG] Registrando observador en propietario: " + propietario.getNombreCompleto());
        propietario.agregarObservador(this);

        return construirRespuestasTablero(propietario);
    }

    @PostMapping("/vistaCerrada")
    public void salir() {
        if (propietario != null) {
            System.out.println("[LOG] Quitando observador de propietario: " + propietario.getNombreCompleto());
            propietario.quitarObservador(this);
        }
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        System.out.println("[LOG] CasoUsoTableroPropietario.actualizar llamado. Evento: " + evento);
        if (evento instanceof Propietario.Eventos ev) {
            switch (ev) {
                case TRANSITO_REALIZADO, SALDO_BAJO, CAMBIO_ESTADO, BONIFICACION_ASIGNADA -> {
                    System.out.println("[LOG] Enviando SSE al propietario: " + propietario.getNombreCompleto());
                    conexionNavegador.enviarJSON(construirRespuestasTablero(propietario));
                }
            }
        }
    }

    @PostMapping("/notificaciones/borrar")
    public Object borrarNotificaciones(@SessionAttribute(name = "usuarioPropietario") Sesion sesion) {
        Propietario p = sesion.getPropietario();
        int cant = p.borrarNotificacionesPropietario();
        if (cant == 0) {
            return ResponseEntity.status(299).body("No hay notificaciones para borrar");
        }
        return Respuesta.lista(
                new Respuesta("notificacionesBorradas", cant),
                new Respuesta("notificaciones", List.of())
        );
    }

    // ----------------- Armado de respuesta del tablero -----------------

    private List<Respuesta> construirRespuestasTablero(Propietario propietario) {
        CabeceraPropietarioDTO cabecera = construirCabecera(propietario);
        List<BonificacionAsignadaDTO> bonis = construirBonificaciones(propietario);
        List<VehiculoResumenDTO> vehs = construirVehiculos(propietario);
        List<TransitoDTO> trans = construirTransitos(propietario);
        List<NotificacionDTO> notifs = construirNotificaciones(propietario);

        return Respuesta.lista(
                new Respuesta("cabecera", cabecera),
                new Respuesta("bonificaciones", bonis),
                new Respuesta("vehiculos", vehs),
                new Respuesta("transitos", trans),
                new Respuesta("notificaciones", notifs));
    }

    private CabeceraPropietarioDTO construirCabecera(Propietario propietario) {
        String estado = propietario.getEstadoPropietario() != null
                ? propietario.getEstadoPropietario().getNombre()
                : "—";
        return new CabeceraPropietarioDTO(
                propietario.getNombreCompleto(),
                estado,
                propietario.getSaldoActual());
    }

    private List<BonificacionAsignadaDTO> construirBonificaciones(Propietario propietario) {
        return propietario.getAsignaciones().stream()
                .map(this::mapearBonificacionAsignada)
                .sorted((b1, b2) -> b1.fechaAsignada.compareTo(b2.fechaAsignada))
                .collect(Collectors.toList());
    }

    private BonificacionAsignadaDTO mapearBonificacionAsignada(AsignacionBonificacion a) {
        return new BonificacionAsignadaDTO(
                a.getBonificacion().getNombre(),
                a.getPuesto().getNombre(),
                a.getFechaAsignacion());
    }

    private List<VehiculoResumenDTO> construirVehiculos(Propietario propietario) {
        return propietario.getVehiculos().stream()
                .map(this::mapearVehiculoResumen)
                .collect(Collectors.toList());
    }

    private VehiculoResumenDTO mapearVehiculoResumen(Vehiculo v) {
        int cant = v.getCantidadTransitos();
        BigDecimal total = v.getMontoTotalGastado();

        return new VehiculoResumenDTO(
                v.getMatricula(),
                v.getModelo(),
                v.getColor(),
                cant,
                total);
    }

    private List<TransitoDTO> construirTransitos(Propietario propietario) {
        return propietario.obtenerTransitosOrdenadosPorFechaDesc().stream()
                .map(this::mapearTransitoDTO)
                .collect(Collectors.toList());
    }

    private TransitoDTO mapearTransitoDTO(Transito t) {
        String nombreBonificacion = t.getBonificacionAplicada() != null
                ? t.getBonificacionAplicada().getNombre()
                : null;

        BigDecimal montoBonificacion = t.getBonificacionAplicada() != null
                ? t.getMontoBase().subtract(t.getMontoCobrado())
                : BigDecimal.ZERO;

        return new TransitoDTO(
                t.getPuesto().getNombre(),
                t.getVehiculo().getMatricula(),
                t.getTarifaAplicada().getCategoria().getNombre(),
                t.getMontoBase(),
                nombreBonificacion,
                montoBonificacion,
                t.getMontoCobrado(),
                t.getFecha(),
                t.getHora());
    }

    private List<NotificacionDTO> construirNotificaciones(Propietario propietario) {
        return propietario.obtenerNotificacionesOrdenadasDesc().stream()
                .map(this::mapearNotificacionDTO)
                .collect(Collectors.toList());
    }

    private NotificacionDTO mapearNotificacionDTO(Notificacion n) {
        return new NotificacionDTO(
                n.getFechaHora(),
                n.getMensaje());
    }
}
