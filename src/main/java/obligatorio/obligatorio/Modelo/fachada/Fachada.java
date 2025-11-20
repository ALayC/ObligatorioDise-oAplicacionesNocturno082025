package obligatorio.obligatorio.Modelo.fachada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.DTO.TarifaDTO;
import obligatorio.obligatorio.Modelo.modelos.Administrador;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Puesto;
import obligatorio.obligatorio.Modelo.modelos.Sesion;
import obligatorio.obligatorio.Modelo.modelos.Transito;
import obligatorio.obligatorio.Modelo.sistemas.SistemaAcceso;
import obligatorio.obligatorio.Modelo.sistemas.SistemaTransito;
import obligatorio.obligatorio.observador.Observable;

public class Fachada extends Observable {
    private final SistemaAcceso sAcceso = new SistemaAcceso();
    private final SistemaTransito sTransito = new SistemaTransito(sAcceso);
    private static final Fachada INST = new Fachada();

    public static Fachada getInstancia() {
        return INST;
    }

    private Fachada() {
    }

    /** Eventos expuestos para los observadores. */
    public enum Eventos {
        cambioListaSesiones,
        transitoRegistrado,
        saldoActualizado,
        notificacionesActualizadas
    }

    /**
     * Datos precargados (administradores, propietarios, puestos, categorías, etc.).
     */
    private final obligatorio.obligatorio.Modelo.modelos.PrecargaDatos precargaDatos;
    {
        obligatorio.obligatorio.Modelo.modelos.PrecargaDatos tempPrecarga = null;
        try {
            tempPrecarga = obligatorio.obligatorio.Modelo.modelos.PrecargaDatos.crear();
        } catch (ObligatorioException e) {
            System.err.println("Error al precargar datos: " + e.getMessage());
        }
        precargaDatos = tempPrecarga;
    }

    /**
     * Registra los datos precargados en los sistemas internos. Llamar tras
     * inicialización.
     */
    public void registrarDatosPrecargados() throws ObligatorioException {
        // Administradores
        for (obligatorio.obligatorio.Modelo.modelos.Administrador admin : precargaDatos.getAdministradores()) {
            agregarAdministrador(admin.getCedula(), admin.getPassword(), admin.getNombreCompleto());
        }
        // Propietarios
        for (obligatorio.obligatorio.Modelo.modelos.Propietario prop : precargaDatos.getPropietarios()) {
            registrarPropietario(prop);
        }
        // Puestos
        for (obligatorio.obligatorio.Modelo.modelos.Puesto puesto : precargaDatos.getPuestos()) {
            agregarPuesto(puesto);
        }
    }

    // Caso de uso 1: login propietario
    public Sesion loginPropietario(String cedula, String pwd) throws ObligatorioException {
        return sAcceso.loginPropietario(cedula, pwd);
    }

    public Administrador loginAdministrador(String cedula, String pwd) throws ObligatorioException {
        return sAcceso.loginAdministrador(cedula, pwd);
    }

    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
            BigDecimal saldo, BigDecimal saldoMin, String nombreEstado)
            throws ObligatorioException {
        sAcceso.agregarPropietario(cedula, pwd, nombreCompleto, saldo, saldoMin);
    }

    public void registrarPropietario(Propietario p) throws ObligatorioException {
        sAcceso.agregarPropietario(p);
    }

    public void agregarAdministrador(String cedula, String pwd, String nombreCompleto)
            throws ObligatorioException {
        sAcceso.agregarAdministrador(cedula, pwd, nombreCompleto);
    }

    public List<Sesion> getSesiones() {
        return sAcceso.getSesiones();
    }

    public void logout(Sesion s) {
        sAcceso.logout(s);
    }

    public void logoutAdministrador(String cedula) {
        sAcceso.logoutAdministrador(cedula);
    }

    // Métodos para emular tránsito
    public void agregarPuesto(Puesto p) throws ObligatorioException {
        sTransito.agregarPuesto(p);
    }

    public List<Puesto> getPuestos() {
        return sTransito.getPuestos();
    }

    public List<PuestoDTO> getPuestosDTO() {
        return sTransito.getPuestosDTO();
    }

    public List<TarifaDTO> getTarifasPorPuesto(String nombrePuesto) throws ObligatorioException {
        return sTransito.getTarifasPorPuesto(nombrePuesto);
    }

    public Transito emularTransito(String matricula, String nombrePuesto, LocalDate fecha, LocalTime hora)
            throws ObligatorioException {
        return sTransito.emularTransito(matricula, nombrePuesto, fecha, hora);
    }

    /** Devuelve la lista de bonificaciones definidas en el sistema. */
    public List<obligatorio.obligatorio.Modelo.modelos.Bonificacion> getBonificacionesDefinidas() {
        return precargaDatos.getBonificaciones();
    }

    /** Devuelve la lista de estados de propietario definidos en el sistema. */
    public List<String> getEstadosPropietarioDefinidos() {
        return precargaDatos.getEstadosPropietario();
    }

    // Este get transitos es para que el caso de uso del admin pueda obtener los
    // transitos, no es para el del propietario, por eso no rompe nada
    public List<Transito> getTransitos() {
        return sTransito.getTransitos();
    }

    /** Devuelve el propietario por cédula, o null si no existe. */
    public Propietario getPropietarioPorCedula(String cedula) {
        return sAcceso.getPropietarioPorCedula(cedula);
    }

    /** Cambia el estado del propietario y registra la notificación. */
    public String cambiarEstadoPropietarioYNotificar(String cedula, String nuevoEstado) {
        return sAcceso.cambiarEstadoPropietarioYNotificar(cedula, nuevoEstado);
    }
}
