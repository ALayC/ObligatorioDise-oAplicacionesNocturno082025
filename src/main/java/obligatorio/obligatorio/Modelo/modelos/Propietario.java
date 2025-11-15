package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import obligatorio.obligatorio.observador.Observable;

public final class Propietario extends Observable {
    
    public enum Eventos { 
        TRANSITO_REALIZADO, 
        SALDO_BAJO 
    }
    
    private String cedula;
    private String password;
    private String nombreCompleto;
    private BigDecimal saldoActual;
    private BigDecimal saldoMinimoAlerta;
    private Estado estadoActual;
    private final Set<Vehiculo> vehiculos = new HashSet<>();
    private final List<Notificacion> notificaciones = new ArrayList<>();
    private final Set<AsignacionBonificacion> asignaciones = new HashSet<>();

    public Propietario(String cedula, String password, String nombreCompleto,
                       BigDecimal saldoActual, BigDecimal saldoMinimoAlerta, Estado estadoActual) {
        this.cedula = Objects.requireNonNull(cedula);
        this.password = Objects.requireNonNull(password);
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto);
        this.saldoActual = Objects.requireNonNull(saldoActual);
        this.saldoMinimoAlerta = Objects.requireNonNull(saldoMinimoAlerta);
        this.estadoActual = Objects.requireNonNull(estadoActual);
    }

    // Getters y setters de Usuario
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public BigDecimal getSaldoActual() { return saldoActual; }
    public void setSaldoActual(BigDecimal saldoActual) { this.saldoActual = saldoActual; }
    public BigDecimal getSaldoMinimoAlerta() { return saldoMinimoAlerta; }
    public void setSaldoMinimoAlerta(BigDecimal saldoMinimoAlerta) { this.saldoMinimoAlerta = saldoMinimoAlerta; }
    public Estado getEstadoActual() { return estadoActual; }
    public void setEstadoActual(Estado estadoActual) { this.estadoActual = estadoActual; }
    public Set<Vehiculo> getVehiculos() { return Collections.unmodifiableSet(vehiculos); }
    public List<Notificacion> getNotificaciones() { return Collections.unmodifiableList(notificaciones); }
    public Set<AsignacionBonificacion> getAsignaciones() { return Collections.unmodifiableSet(asignaciones); }
    public boolean agregarVehiculo(Vehiculo v){ return vehiculos.add(v); }
    public void agregarNotificacion(Notificacion n){ notificaciones.add(n); }
    public boolean agregarAsignacion(AsignacionBonificacion a){ return asignaciones.add(a); }
    public int cantidadNotificaciones() { return notificaciones.size(); }
    public void limpiarNotificaciones() { notificaciones.clear(); }

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
    public boolean equals(Object o){ return o instanceof Propietario p && getCedula().equals(p.getCedula()); }
    @Override
    public int hashCode(){ return Objects.hash(getCedula()); }
}
