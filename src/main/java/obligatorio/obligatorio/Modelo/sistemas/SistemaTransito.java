package obligatorio.obligatorio.Modelo.sistemas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.DTO.ResultadoEmulacionDTO;
import obligatorio.obligatorio.DTO.TarifaDTO;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.Bonificacion;
import obligatorio.obligatorio.Modelo.modelos.Categoria;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Puesto;
import obligatorio.obligatorio.Modelo.modelos.Tarifa;
import obligatorio.obligatorio.Modelo.modelos.Transito;
import obligatorio.obligatorio.Modelo.modelos.Vehiculo;

public class SistemaTransito {

    private final SistemaAcceso sistemaAcceso; 
    private final List<Transito> transitos = new ArrayList<>();
    private final List<Puesto> puestos = new ArrayList<>();

    public SistemaTransito(SistemaAcceso sistemaAcceso) {
        this.sistemaAcceso = sistemaAcceso;
    }

    // --- Gestión de puestos ---
    public void agregarPuesto(Puesto p) throws ObligatorioException {
        if (p == null)
            throw new ObligatorioException("Puesto nulo");
        if (puestos.contains(p))
            throw new ObligatorioException("El puesto ya existe");
        puestos.add(p);
    }

    public List<Puesto> getPuestos() {
        return puestos;
    }

    public List<PuestoDTO> getPuestosDTO() {
        return puestos.stream()
                .map(p -> new PuestoDTO(p.getNombre(), p.getDireccion()))
                .collect(Collectors.toList());
    }

    public List<TarifaDTO> getTarifasPorPuesto(String nombrePuesto) throws ObligatorioException {
        Puesto puesto = puestos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombrePuesto))
                .findFirst()
                .orElseThrow(() -> new ObligatorioException("Puesto no encontrado"));
        return puesto.getTarifas().stream()
                .map(t -> new TarifaDTO(t.getCategoria().getNombre(), t.getMonto()))
                .collect(Collectors.toList());
    }

    // --- Gestión de tránsitos ---
    public void registrarTransito(Transito t) {
        if (t != null)
            transitos.add(t);
    }

    public List<Transito> getTransitos() {
        return transitos;
    }

    /** Caso de uso: Emular tránsito (orquestador). */
    public ResultadoEmulacionDTO emularTransito(String matricula, String nombrePuesto, LocalDate fecha, LocalTime hora)
            throws ObligatorioException {
        validarParametros(matricula, nombrePuesto, fecha, hora);

        Vehiculo vehiculo = buscarVehiculo(matricula);
        Propietario propietario = vehiculo.getPropietario();

        propietario.validarPuedeRealizarTransito();

        Puesto puesto = buscarPuesto(nombrePuesto);
        Tarifa tarifa = buscarTarifa(puesto, vehiculo.getCategoria());

        // Usa el experto Propietario para obtener la bonificación (ya respeta
        // "Penalizado")
        Bonificacion bonificacion = propietario.obtenerBonificacionPara(puesto);

        // ⬇⬇⬇ AQUÍ el cambio importante: pasamos TODO el contexto
        BigDecimal montoCobrado = calcularMontoCobrado(
                tarifa,
                bonificacion,
                propietario,
                vehiculo,
                puesto,
                fecha);

        propietario.cobrarTransito(montoCobrado);

        registrarTransitoInterno(vehiculo, puesto, tarifa, fecha, hora, bonificacion, montoCobrado);

        propietario.registrarNotificacionTransito(puesto, vehiculo, fecha, hora);

        propietario.verificarSaldoBajoYNotificar();

        emitirEventosGlobales();

        return construirResultado(propietario, vehiculo, tarifa, bonificacion, montoCobrado);
    }

    // -------- Helpers privados (mantienen comportamiento original) --------
    private void validarParametros(String matricula, String nombrePuesto, LocalDate fecha, LocalTime hora)
            throws ObligatorioException {
        if (matricula == null || matricula.isBlank())
            throw new ObligatorioException("Matrícula requerida");
        if (nombrePuesto == null || nombrePuesto.isBlank())
            throw new ObligatorioException("Puesto requerido");
        if (fecha == null)
            throw new ObligatorioException("Fecha requerida");
        if (hora == null)
            throw new ObligatorioException("Hora requerida");
    }

    private Vehiculo buscarVehiculo(String matricula) throws ObligatorioException {
        for (Propietario p : sistemaAcceso.getPropietarios()) {
            for (Vehiculo v : p.getVehiculos()) {
                if (v.getMatricula().equalsIgnoreCase(matricula))
                    return v;
            }
        }
        throw new ObligatorioException("No existe el vehículo");
    }

    private Puesto buscarPuesto(String nombrePuesto) throws ObligatorioException {
        for (Puesto p : puestos) {
            if (p.getNombre().equalsIgnoreCase(nombrePuesto))
                return p;
        }
        throw new ObligatorioException("Puesto no encontrado");
    }

    private Tarifa buscarTarifa(Puesto puesto, Categoria categoria) throws ObligatorioException {
        for (Tarifa t : puesto.getTarifas()) {
            if (t.getCategoria().equals(categoria))
                return t;
        }
        throw new ObligatorioException("No existe tarifa para la categoría del vehículo en este puesto");
    }

    private BigDecimal calcularMontoCobrado(Tarifa tarifa,
            Bonificacion bonificacion,
            Propietario propietario,
            Vehiculo vehiculo,
            Puesto puesto,
            LocalDate fecha) {

        BigDecimal base = tarifa.getMonto();

        if (bonificacion == null) {
            return base;
        }
        return bonificacion.calcularMonto(
                base,
                propietario,
                vehiculo,
                puesto,
                fecha,
                transitos);
    }

    private void registrarTransitoInterno(Vehiculo vehiculo, Puesto puesto, Tarifa tarifa, LocalDate fecha,
            LocalTime hora,
            Bonificacion bonificacion, BigDecimal montoCobrado) {
        Transito t = new Transito(
                vehiculo,
                puesto,
                tarifa,
                fecha,
                hora,
                tarifa.getMonto(),
                montoCobrado,
                bonificacion,
                true);
        transitos.add(t);
    }

    private void emitirEventosGlobales() {
        Fachada.getInstancia().avisar(Fachada.Eventos.transitoRegistrado);
        Fachada.getInstancia().avisar(Fachada.Eventos.saldoActualizado);
        Fachada.getInstancia().avisar(Fachada.Eventos.notificacionesActualizadas);
    }

    private ResultadoEmulacionDTO construirResultado(Propietario propietario, Vehiculo vehiculo, Tarifa unused,
            Bonificacion bonificacion, BigDecimal montoCobrado) {
        ResultadoEmulacionDTO dto = new ResultadoEmulacionDTO();
        dto.setNombrePropietario(propietario.getNombreCompleto());
        dto.setEstado(propietario.getEstadoPropietario().getNombre());
        dto.setCategoria(vehiculo.getCategoria().getNombre());
        dto.setBonificacion(bonificacion != null ? bonificacion.getNombre() : null);
        dto.setCostoTransito(montoCobrado);
        dto.setSaldoDespues(propietario.getSaldoActual());
        return dto;
    }
}
