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
import obligatorio.obligatorio.Modelo.modelos.AsignacionBonificacion;
import obligatorio.obligatorio.Modelo.modelos.Bonificacion;
import obligatorio.obligatorio.Modelo.modelos.Categoria;
import obligatorio.obligatorio.Modelo.modelos.Notificacion;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Puesto;
import obligatorio.obligatorio.Modelo.modelos.Tarifa;
import obligatorio.obligatorio.Modelo.modelos.Transito;
import obligatorio.obligatorio.Modelo.modelos.Vehiculo;

public class SistemaTransito {

    private final SistemaAcceso sistemaAcceso; // fuente de propietarios/vehículos
    private final List<Transito> transitos = new ArrayList<>();
    private final List<Puesto> puestos = new ArrayList<>();

    public SistemaTransito(SistemaAcceso sistemaAcceso) {
        this.sistemaAcceso = sistemaAcceso;
    }

    // --- Gestión de puestos ---
    public void agregarPuesto(Puesto p) throws ObligatorioException {
        if (p == null) throw new ObligatorioException("Puesto nulo");
        if (puestos.contains(p)) throw new ObligatorioException("El puesto ya existe");
        puestos.add(p);
    }
    public List<Puesto> getPuestos() { return puestos; }
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
    public void registrarTransito(Transito t) { if (t != null) transitos.add(t); }
    public List<Transito> getTransitos() { return transitos; }


    /** Caso de uso: Emular tránsito (orquestador). */
    public ResultadoEmulacionDTO emularTransito(String matricula, String nombrePuesto, LocalDate fecha, LocalTime hora)
            throws ObligatorioException {
        validarParametros(matricula, nombrePuesto, fecha, hora);
        Vehiculo vehiculo = buscarVehiculo(matricula);
        Propietario propietario = vehiculo.getPropietario();
        validarEstadoPropietario(propietario);
        Puesto puesto = buscarPuesto(nombrePuesto);
        Tarifa tarifa = buscarTarifa(puesto, vehiculo.getCategoria());
        boolean penalizado = esPenalizado(propietario);
        Bonificacion bonificacion = determinarBonificacion(propietario, puesto, penalizado);
        BigDecimal montoCobrado = calcularMontoCobrado(tarifa, bonificacion);
        verificarSaldo(propietario, montoCobrado);
        descontarSaldo(propietario, montoCobrado);
        registrarTransitoInterno(vehiculo, puesto, tarifa, fecha, hora, bonificacion, montoCobrado);
        notificarTransitoSiCorresponde(propietario, vehiculo, puesto, fecha, penalizado);
        notificarSaldoBajoSiCorresponde(propietario);
        emitirEventosGlobales();
        return construirResultado(propietario, vehiculo, tarifa, bonificacion, montoCobrado);
    }

    // -------- Helpers privados (mantienen comportamiento original) --------
    private void validarParametros(String matricula, String nombrePuesto, LocalDate fecha, LocalTime hora) throws ObligatorioException {
        if (matricula == null || matricula.isBlank()) throw new ObligatorioException("Matrícula requerida");
        if (nombrePuesto == null || nombrePuesto.isBlank()) throw new ObligatorioException("Puesto requerido");
        if (fecha == null) throw new ObligatorioException("Fecha requerida");
        if (hora == null) throw new ObligatorioException("Hora requerida");
    }

    private Vehiculo buscarVehiculo(String matricula) throws ObligatorioException {
        for (Propietario p : sistemaAcceso.getPropietarios()) {
            for (Vehiculo v : p.getVehiculos()) {
                if (v.getMatricula().equalsIgnoreCase(matricula)) return v;
            }
        }
        throw new ObligatorioException("No existe el vehículo");
    }

    private void validarEstadoPropietario(Propietario propietario) throws ObligatorioException {
        String estado = propietario.getEstadoActual() != null ? propietario.getEstadoActual().getNombre() : null;
        if (estado == null) return; // Sin estado definido se permite (según lógica actual)
        if (estado.equalsIgnoreCase("Deshabilitado"))
            throw new ObligatorioException("El propietario del vehículo está deshabilitado, no puede realizar tránsitos");
        if (estado.equalsIgnoreCase("Suspendido"))
            throw new ObligatorioException("El propietario del vehículo está suspendido, no puede realizar tránsitos");
    }

    private Puesto buscarPuesto(String nombrePuesto) throws ObligatorioException {
        for (Puesto p : puestos) {
            if (p.getNombre().equalsIgnoreCase(nombrePuesto)) return p;
        }
        throw new ObligatorioException("Puesto no encontrado");
    }

    private Tarifa buscarTarifa(Puesto puesto, Categoria categoria) throws ObligatorioException {
        for (Tarifa t : puesto.getTarifas()) {
            if (t.getCategoria().equals(categoria)) return t;
        }
        throw new ObligatorioException("No existe tarifa para la categoría del vehículo en este puesto");
    }

    private boolean esPenalizado(Propietario propietario) {
        String estado = propietario.getEstadoActual() != null ? propietario.getEstadoActual().getNombre() : null;
        return estado != null && estado.equalsIgnoreCase("Penalizado");
    }

    private Bonificacion determinarBonificacion(Propietario propietario, Puesto puesto, boolean penalizado) {
        if (penalizado) return null;
        for (AsignacionBonificacion asig : propietario.getAsignaciones()) {
            if (asig.getPuesto().equals(puesto)) return asig.getBonificacion();
        }
        return null;
    }

    private BigDecimal calcularMontoCobrado(Tarifa tarifa, Bonificacion bonificacion) {
        BigDecimal base = tarifa.getMonto();
        return bonificacion != null ? bonificacion.calcularMonto(base) : base;
    }

    private void verificarSaldo(Propietario propietario, BigDecimal monto) throws ObligatorioException {
        if (propietario.getSaldoActual().compareTo(monto) < 0)
            throw new ObligatorioException("Saldo insuficiente: " + propietario.getSaldoActual());
    }

    private void descontarSaldo(Propietario propietario, BigDecimal monto) {
        propietario.setSaldoActual(propietario.getSaldoActual().subtract(monto));
    }

    private void registrarTransitoInterno(Vehiculo vehiculo, Puesto puesto, Tarifa tarifa, LocalDate fecha, LocalTime hora,
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
            true
        );
        transitos.add(t);
    }

        private void notificarTransitoSiCorresponde(Propietario propietario, Vehiculo vehiculo, Puesto puesto,
                            LocalDate fechaHora, boolean penalizado) {
        if (penalizado) return;
        String mensaje = String.format("%s Pasaste por el puesto %s con el vehículo %s",
            fechaHora.toString(), puesto.getNombre(), vehiculo.getMatricula());
        propietario.agregarNotificacion(new Notificacion(mensaje, LocalDate.now()));
        propietario.avisar(Propietario.Eventos.TRANSITO_REALIZADO);
        }

    private void notificarSaldoBajoSiCorresponde(Propietario propietario) {
        if (propietario.getSaldoActual().compareTo(propietario.getSaldoMinimoAlerta()) < 0) {
            String mensaje = String.format("%s Tu saldo actual es de $%s. Te recomendamos hacer una recarga",
                    LocalDate.now().toString(), propietario.getSaldoActual());
            propietario.agregarNotificacion(new Notificacion(mensaje, LocalDate.now()));
            propietario.avisar(Propietario.Eventos.SALDO_BAJO);
        }
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
        dto.setEstado(propietario.getEstadoActual().getNombre());
        dto.setCategoria(vehiculo.getCategoria().getNombre());
        dto.setBonificacion(bonificacion != null ? bonificacion.getNombre() : null);
        dto.setCostoTransito(montoCobrado);
        dto.setSaldoDespues(propietario.getSaldoActual());
        return dto;
    }
}
