package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import obligatorio.obligatorio.Controladores.Respuesta;
import obligatorio.obligatorio.DTO.BonificacionAsignadaDTO;
import obligatorio.obligatorio.DTO.CabeceraPropietarioDTO;
import obligatorio.obligatorio.DTO.NotificacionDTO;
import obligatorio.obligatorio.DTO.TransitoDTO;
import obligatorio.obligatorio.DTO.VehiculoResumenDTO;
import obligatorio.obligatorio.observador.Observable;

public final class Propietario extends Observable {

    public enum Eventos {
        TRANSITO_REALIZADO,
        SALDO_BAJO,
        CAMBIO_ESTADO,
        BONIFICACION_ASIGNADA
    }

    private String cedula;
    private String password;
    private String nombreCompleto;
    private BigDecimal saldoActual;
    private BigDecimal saldoMinimoAlerta;
    private EstadoPropietario estadoPropietario;
    private final Set<Vehiculo> vehiculos = new HashSet<>();
    private final List<Notificacion> notificaciones = new ArrayList<>();
    private final Set<AsignacionBonificacion> asignaciones = new HashSet<>();

    public Propietario(String cedula, String password, String nombreCompleto,
            BigDecimal saldoActual, BigDecimal saldoMinimoAlerta) {
        this.cedula = Objects.requireNonNull(cedula);
        this.password = Objects.requireNonNull(password);
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto);
        this.saldoActual = Objects.requireNonNull(saldoActual);
        this.saldoMinimoAlerta = Objects.requireNonNull(saldoMinimoAlerta);
        this.estadoPropietario = FabricaEstadoPropietario.crearEstado("Habilitado", this);
    }

    // Getters y setters de Usuario
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public BigDecimal getSaldoMinimoAlerta() {
        return saldoMinimoAlerta;
    }

    public void setSaldoMinimoAlerta(BigDecimal saldoMinimoAlerta) {
        this.saldoMinimoAlerta = saldoMinimoAlerta;
    }

    public EstadoPropietario getEstadoPropietario() {
        return estadoPropietario;
    }

    public void setEstadoPropietario(EstadoPropietario estadoPropietario) {
        this.estadoPropietario = Objects.requireNonNull(estadoPropietario);
    }

    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        estadoPropietario.asignarBonificacion(bonificacion, puesto);
    }

    public Set<Vehiculo> getVehiculos() {
        return Collections.unmodifiableSet(vehiculos);
    }

    public List<Notificacion> getNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }

    public Set<AsignacionBonificacion> getAsignaciones() {
        return Collections.unmodifiableSet(asignaciones);
    }

    public boolean agregarVehiculo(Vehiculo v) {
        return vehiculos.add(v);
    }

    public void agregarNotificacion(Notificacion n) {
        notificaciones.add(n);
    }

    public boolean agregarAsignacion(AsignacionBonificacion a) {
        return asignaciones.add(a);
    }

    public int cantidadNotificaciones() {
        return notificaciones.size();
    }

    public void limpiarNotificaciones() {
        notificaciones.clear();
    }

    // Verifica si el propietario ya tiene una bonificación asignada para el puesto
    // dado
    public boolean tieneBonificacionParaPuesto(Puesto puesto) {
        for (AsignacionBonificacion asignacion : asignaciones) {
            if (asignacion.getPuesto().equals(puesto)) {
                return true;
            }
        }
        return false;
    }

    public void asignarBonificacionInterna(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        Objects.requireNonNull(bonificacion, "Bonificación requerida");
        Objects.requireNonNull(puesto, "Puesto requerido");

        if (tieneBonificacionParaPuesto(puesto)) {
            throw new ObligatorioException("Ya tiene una bonificación asignada para ese puesto");
        }

        AsignacionBonificacion asignacion = new AsignacionBonificacion(
                this,
                puesto,
                bonificacion,
                LocalDate.now());

        agregarAsignacionBonificacion(asignacion);
    }

    // Agrega una asignación de bonificación al propietario
    public void agregarAsignacionBonificacion(AsignacionBonificacion asignacion) {
        asignaciones.add(asignacion);
        avisar(Eventos.BONIFICACION_ASIGNADA);
    }

    @Override
    public void agregarObservador(obligatorio.obligatorio.observador.Observador o) {
        super.agregarObservador(o);
        System.out.println("[LOG] Observador agregado a propietario: " + nombreCompleto);
    }

    @Override
    public void quitarObservador(obligatorio.obligatorio.observador.Observador o) {
        super.quitarObservador(o);
        System.out.println("[LOG] Observador quitado de propietario: " + nombreCompleto);
    }

    @Override
    public void avisar(Object evento) {
        System.out.println("[LOG] Propietario.avisar llamado. Evento: " + evento + " para " + nombreCompleto);
        super.avisar(evento);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Propietario p && getCedula().equals(p.getCedula());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCedula());
    }

    public String cambiarEstadoYNotificar(String nuevoEstado) {
        if (estadoPropietario == null) {
            throw new IllegalStateException("El propietario no tiene un estado asignado.");
        }

        // Si el estado ya es el mismo, devolver mensaje sin cambios
        if (estadoPropietario.getNombre().equalsIgnoreCase(nuevoEstado)) {
            return "El propietario ya está en estado " + estadoPropietario.getNombre();
        }

        // Crear el nuevo estado usando la fábrica (puede lanzar excepción si es
        // inválido)
        EstadoPropietario nuevo = FabricaEstadoPropietario.crearEstado(nuevoEstado, this);

        // Aplicar el nuevo estado
        this.estadoPropietario = nuevo;

        // Registrar notificación (este CU exige SIEMPRE registrar la notificación)
        notificaciones.add(new Notificacion(
                "Se ha cambiado tu estado en el sistema. Tu estado actual es " + nuevo.getNombre(),
                LocalDate.now()));

        // Avisar a los observadores
        avisar(Eventos.CAMBIO_ESTADO);

        return "Estado cambiado correctamente";
    }

    // ----------------- Tablero propietario (CU 1) -----------------



    //TODO: ESTO ESTA MAL, SE ARMA EL DTO EN EL CONTROLLER

    // refactor tablero propietario
    public List<Respuesta> armarRespuestasTablero(List<Transito> todosLosTransitos) {
        CabeceraPropietarioDTO cabecera = construirCabecera();
        List<BonificacionAsignadaDTO> bonis = construirBonificaciones();
        List<VehiculoResumenDTO> vehs = construirVehiculos(todosLosTransitos);
        List<TransitoDTO> trans = construirTransitos(todosLosTransitos);
        List<NotificacionDTO> notifs = construirNotificaciones();

        return Respuesta.lista(
                new Respuesta("cabecera", cabecera),
                new Respuesta("bonificaciones", bonis),
                new Respuesta("vehiculos", vehs),
                new Respuesta("transitos", trans),
                new Respuesta("notificaciones", notifs));
    }

    public int borrarNotificacionesPropietario() {
        int cant = cantidadNotificaciones();
        if (cant > 0) {
            limpiarNotificaciones();
        }
        return cant;
    }

    private CabeceraPropietarioDTO construirCabecera() {
        String estado = estadoPropietario != null ? estadoPropietario.getNombre() : "—";
        return new CabeceraPropietarioDTO(
                nombreCompleto,
                estado,
                saldoActual);
    }

    private List<BonificacionAsignadaDTO> construirBonificaciones() {
        return asignaciones.stream()
                .map(a -> new BonificacionAsignadaDTO(
                        a.getBonificacion().getNombre(),
                        a.getPuesto().getNombre(),
                        a.getFechaAsignacion()))
                .sorted(Comparator.comparing(b -> b.fechaAsignada))
                .collect(Collectors.toList());
    }

    private List<VehiculoResumenDTO> construirVehiculos(List<Transito> todosLosTransitos) {
        List<Transito> transitosProp = filtrarTransitosDelPropietario(todosLosTransitos);

        return vehiculos.stream()
                .map(v -> {
                    List<Transito> transV = transitosProp.stream()
                            .filter(t -> t.getVehiculo().equals(v))
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

    private List<TransitoDTO> construirTransitos(List<Transito> todosLosTransitos) {
        List<Transito> transitosProp = filtrarTransitosDelPropietario(todosLosTransitos);

        return transitosProp.stream()
                .sorted(Comparator.comparing(Transito::getFecha)
                        .thenComparing(Transito::getHora)
                        .reversed())
                .map(t -> new TransitoDTO(
                        t.getPuesto().getNombre(),
                        t.getVehiculo().getMatricula(),
                        t.getTarifaAplicada().getCategoria().getNombre(),
                        t.getMontoBase(),
                        t.getBonificacionAplicada() != null ? t.getBonificacionAplicada().getNombre() : null,
                        t.getBonificacionAplicada() != null
                                ? t.getMontoBase().subtract(t.getMontoCobrado())
                                : BigDecimal.ZERO,
                        t.getMontoCobrado(),
                        t.getFecha(),
                        t.getHora()))
                .collect(Collectors.toList());
    }

    private List<NotificacionDTO> construirNotificaciones() {
        return notificaciones.stream()
                .map(n -> new NotificacionDTO(
                        n.getFechaHora(),
                        n.getMensaje()))
                .sorted(Comparator.comparing((NotificacionDTO n) -> n.fechaHora).reversed())
                .collect(Collectors.toList());
    }

    private List<Transito> filtrarTransitosDelPropietario(List<Transito> todosLosTransitos) {
        Set<String> matriculasProp = vehiculos.stream()
                .map(Vehiculo::getMatricula)
                .collect(Collectors.toSet());

        return todosLosTransitos.stream()
                .filter(t -> matriculasProp.contains(t.getVehiculo().getMatricula()))
                .collect(Collectors.toList());
    }

    // ----------------- Lógica de tránsito -----------------

    public void validarPuedeRealizarTransito() throws ObligatorioException {
        if (estadoPropietario == null) {
            return;
        }
        estadoPropietario.validarPuedeRealizarTransito();
    }

    public Bonificacion obtenerBonificacionPara(Puesto puesto) {
        if (estadoPropietario == null || !estadoPropietario.permiteAplicarBonificaciones()) {
            return null;
        }
        for (AsignacionBonificacion asig : asignaciones) {
            if (asig.getPuesto().equals(puesto)) {
                return asig.getBonificacion();
            }
        }
        return null;
    }

    public void cobrarTransito(BigDecimal monto) throws ObligatorioException {
        if (saldoActual.compareTo(monto) < 0) {
            throw new ObligatorioException("Saldo insuficiente: " + saldoActual);
        }
        saldoActual = saldoActual.subtract(monto);
    }

    public void registrarNotificacionTransito(Puesto puesto, Vehiculo vehiculo, LocalDate fecha, LocalTime hora) {
        if (estadoPropietario == null || !estadoPropietario.registraNotificaciones()) {
            return;
        }
        String mensaje = String.format("%s %s Pasaste por el puesto %s con el vehículo %s",
                fecha.toString(), hora.toString(), puesto.getNombre(), vehiculo.getMatricula());
        notificaciones.add(new Notificacion(mensaje, LocalDate.now()));
        avisar(Eventos.TRANSITO_REALIZADO);
    }

    public void verificarSaldoBajoYNotificar() {
        if (estadoPropietario == null || !estadoPropietario.registraNotificaciones()) {
            return;
        }
        if (saldoActual.compareTo(saldoMinimoAlerta) < 0) {
            String mensaje = String.format("%s Tu saldo actual es de $%s. Te recomendamos hacer una recarga",
                    LocalDate.now().toString(), saldoActual);
            notificaciones.add(new Notificacion(mensaje, LocalDate.now()));
            avisar(Eventos.SALDO_BAJO);
        }
    }

}
