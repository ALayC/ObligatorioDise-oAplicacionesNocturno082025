package obligatorio.obligatorio.Modelo;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import obligatorio.obligatorio.Controladores.Respuesta;
import obligatorio.obligatorio.DTO.BonificacionAsignadaDTO;
import obligatorio.obligatorio.DTO.CabeceraPropietarioDTO;
import obligatorio.obligatorio.DTO.NotificacionDTO;
import obligatorio.obligatorio.DTO.TransitoDTO;
import obligatorio.obligatorio.DTO.VehiculoResumenDTO;

final class SistemaTablero {

    List<Respuesta> armarRespuestasTablero(Propietario p) {
        CabeceraPropietarioDTO cabecera = construirCabecera(p);
        List<BonificacionAsignadaDTO> bonis = construirBonificaciones(p);
        List<VehiculoResumenDTO> vehs = construirVehiculos(p);
        List<TransitoDTO> trans = construirTransitos(p); // hoy vacío si no hay sistema de tránsitos
        List<NotificacionDTO> notifs = construirNotificaciones(p);

        return Respuesta.lista(
                new Respuesta("cabecera", cabecera),
                new Respuesta("bonificaciones", bonis),
                new Respuesta("vehiculos", vehs),
                new Respuesta("transitos", trans),
                new Respuesta("notificaciones", notifs));
    }

    int borrarNotificaciones(Propietario p) {
        int cant = p.cantidadNotificaciones();
        if (cant > 0) {
            p.limpiarNotificaciones();
        }
        return cant;
    }

    // ------------------- helpers -------------------

    private CabeceraPropietarioDTO construirCabecera(Propietario p) {
        String estado = p.getEstadoActual() != null ? p.getEstadoActual().getNombre() : "—";
        return new CabeceraPropietarioDTO(
                p.getNombreCompleto(),
                estado,
                p.getSaldoActual());
    }

    private List<BonificacionAsignadaDTO> construirBonificaciones(Propietario p) {
        return p.getAsignaciones().stream()
                .map(a -> new BonificacionAsignadaDTO(
                        a.getBonificacion().getNombre(),
                        a.getPuesto().getNombre(),
                        a.getFechaAsignacion()))
                .sorted(Comparator.comparing(b -> b.fechaAsignada)) // asc; usar .reversed() si querés desc
                .collect(Collectors.toList());
    }

    private List<VehiculoResumenDTO> construirVehiculos(Propietario p) {
        var transitosProp = Fachada.getInstancia().getTransitos();
        return p.getVehiculos().stream()
                .map(v -> {
                    var transV = transitosProp.stream()
                            .filter(t -> t.getVehiculo().getMatricula().equalsIgnoreCase(v.getMatricula()))
                            .toList();

                    int cant = transV.size();
                    BigDecimal total = transV.stream()
                            .map(Transito::getMontoCobrado)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new VehiculoResumenDTO(
                            v.getMatricula(),
                            v.getModelo(),
                            v.getColor(),
                            cant,
                            total);
                })
                .collect(Collectors.toList());
    }

    private List<TransitoDTO> construirTransitos(Propietario p) {
        var matriculasProp = p.getVehiculos().stream()
                .map(Vehiculo::getMatricula)
                .collect(Collectors.toSet());

        return Fachada.getInstancia().getTransitos().stream()
                .filter(t -> matriculasProp.contains(t.getVehiculo().getMatricula()))
                .sorted(Comparator.comparing(Transito::getFechaHora).reversed())
                .map(t -> new TransitoDTO(
                        t.getPuesto().getNombre(),
                        t.getVehiculo().getMatricula(),
                        t.getTarifaAplicada().getCategoria().getNombre(),
                        t.getMontoBase(),
                        t.getBonificacionAplicada() != null ? t.getBonificacionAplicada().getNombre() : null,
                        t.getBonificacionAplicada() != null ? t.getMontoBase().subtract(t.getMontoCobrado())
                                : BigDecimal.ZERO,
                        t.getMontoCobrado(),
                        t.getFechaHora().toLocalDate(),
                        t.getFechaHora().toLocalTime()))
                .collect(Collectors.toList());
    }

    private List<NotificacionDTO> construirNotificaciones(Propietario p) {
        return p.getNotificaciones().stream()
                .map(n -> new NotificacionDTO(n.getFechaHora(), n.getMensaje()))
                .sorted(Comparator.comparing((NotificacionDTO n) -> n.fechaHora).reversed())
                .collect(Collectors.toList());
    }
}
