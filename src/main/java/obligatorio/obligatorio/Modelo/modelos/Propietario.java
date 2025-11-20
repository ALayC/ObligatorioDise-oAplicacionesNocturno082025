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

    public int borrarNotificacionesPropietario() {
        int cant = cantidadNotificaciones();
        if (cant > 0) {
            limpiarNotificaciones();
        }
        return cant;
    }

    // Verifica si el propietario ya tiene una bonificación asignada para el puesto dado
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

        if (estadoPropietario.getNombre().equalsIgnoreCase(nuevoEstado)) {
            return "El propietario ya está en estado " + estadoPropietario.getNombre();
        }

        EstadoPropietario nuevo = FabricaEstadoPropietario.crearEstado(nuevoEstado, this);

        this.estadoPropietario = nuevo;

        notificaciones.add(new Notificacion(
                "Se ha cambiado tu estado en el sistema. Tu estado actual es " + nuevo.getNombre(),
                LocalDate.now()));

        avisar(Eventos.CAMBIO_ESTADO);

        return "Estado cambiado correctamente";
    }

    // ----------------- Lógica de tablero (dominio, sin DTOs) -----------------

    /**
     * Devuelve todos los tránsitos de todos los vehículos del propietario.
     */
    public List<Transito> obtenerTransitosPropietario() {
        List<Transito> resultado = new ArrayList<>();
        for (Vehiculo v : vehiculos) {
            resultado.addAll(v.getTransitos());
        }
        return resultado;
    }

    /**
     * Devuelve todos los tránsitos del propietario ordenados por fecha/hora descendente.
     */
    public List<Transito> obtenerTransitosOrdenadosPorFechaDesc() {
        List<Transito> resultado = obtenerTransitosPropietario();
        resultado.sort(
                Comparator
                        .comparing(Transito::getFecha)
                        .thenComparing(Transito::getHora)
                        .reversed()
        );
        return resultado;
    }

    /**
     * Devuelve las notificaciones ordenadas por fecha/hora descendente.
     */
    public List<Notificacion> obtenerNotificacionesOrdenadasDesc() {
        List<Notificacion> copia = new ArrayList<>(notificaciones);
        copia.sort(
                Comparator
                        .comparing(Notificacion::getFechaHora)
                        .reversed()
        );
        return copia;
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
