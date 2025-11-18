package obligatorio.obligatorio.Modelo.fachada;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import obligatorio.obligatorio.Controladores.Respuesta;
import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.DTO.ResultadoEmulacionDTO;
import obligatorio.obligatorio.DTO.TarifaDTO;
import obligatorio.obligatorio.Modelo.modelos.Administrador;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Puesto;
import obligatorio.obligatorio.Modelo.modelos.Sesion;
import obligatorio.obligatorio.Modelo.modelos.Transito;
import obligatorio.obligatorio.Modelo.sistemas.SistemaAcceso;
import obligatorio.obligatorio.Modelo.sistemas.SistemaTablero;
import obligatorio.obligatorio.Modelo.sistemas.SistemaTransito;
import obligatorio.obligatorio.observador.Observable;

/**
 * Fachada del sistema: punto único de acceso y único Observable.
 * Centraliza los avisos de eventos del dominio hacia los observadores (controladores en sesión, etc.).
 */
public class Fachada extends Observable {
            /** Cambia el estado del propietario y registra la notificación. */
    public String cambiarEstadoPropietarioYNotificar(String cedula, String nuevoEstado) {
        Propietario propietario = getPropietarioPorCedula(cedula);
        if (propietario == null) {
            return "No existe el propietario";
        }
        return propietario.cambiarEstadoYNotificar(nuevoEstado);
            }


        /** Registra los datos precargados en los sistemas internos. Llamar tras inicialización. */
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
            // Si necesitas registrar tránsitos, bonificaciones, etc., agrégalos aquí
        }
    /** Eventos expuestos para los observadores. */
    public enum Eventos {
        cambioListaSesiones,
        transitoRegistrado,
        saldoActualizado,
        notificacionesActualizadas
    }

    private static final Fachada INST = new Fachada();
    public static Fachada getInstancia() { return INST; }
    private Fachada() {}

    private final SistemaAcceso sAcceso = new SistemaAcceso();
    private final SistemaTablero sTablero = new SistemaTablero();
    private final SistemaTransito sTransito = new SistemaTransito(sAcceso);
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

    /** Registrar un tránsito y avisar a los observadores. */
    public void registrarTransito(Transito t) {
        sTransito.registrarTransito(t);
        avisar(Eventos.transitoRegistrado);
    }
    
    public List<Transito> getTransitos() { return sTransito.getTransitos(); }

    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
                                   BigDecimal saldo, BigDecimal saldoMin, String nombreEstado)
            throws ObligatorioException {
        sAcceso.agregarPropietario(cedula, pwd, nombreCompleto, saldo, saldoMin, nombreEstado);
        // Podría disparar evento de cambio de sesiones si se refleja en vistas de administración
    }

    public void registrarPropietario(Propietario p) throws ObligatorioException {
        sAcceso.agregarPropietario(p);
    }

    public void agregarAdministrador(String cedula, String pwd, String nombreCompleto)
            throws ObligatorioException {
        sAcceso.agregarAdministrador(cedula, pwd, nombreCompleto);
    }

    public Sesion loginPropietario(String cedula, String pwd) throws ObligatorioException {
        Sesion s = sAcceso.loginPropietario(cedula, pwd);
        System.out.println(" Login propietario: " + s.getPropietario().getNombreCompleto() + " - Avisando cambioListaSesiones");
        avisar(Eventos.cambioListaSesiones);
        return s;
    }

    public Administrador loginAdministrador(String cedula, String pwd) throws ObligatorioException {
        Administrador a = sAcceso.loginAdministrador(cedula, pwd);
        avisar(Eventos.cambioListaSesiones);
        return a;
    }

    public List<Sesion> getSesiones() { return sAcceso.getSesiones(); }

    public void logout(Sesion s) {
        sAcceso.logout(s);
        System.out.println("🚪 Logout propietario - Avisando cambioListaSesiones");
        avisar(Eventos.cambioListaSesiones);
    }

    public void logoutAdministrador(String cedula) {
        sAcceso.logoutAdministrador(cedula);
        avisar(Eventos.cambioListaSesiones);
    }

    //TODO: esto se resuelve con los dtos o en contreller no aca en fachada. Este es lopgica no va en el sistema de tablero
    public List<Respuesta> armarRespuestasTablero(Propietario p) { return sTablero.armarRespuestasTablero(p); }


     //esto es logica no va aca
    public int borrarNotificaciones(Propietario p) {
        int borradas = sTablero.borrarNotificaciones(p);
        avisar(Eventos.notificacionesActualizadas);
        return borradas;
    }

    // Métodos para emular tránsito
    public void agregarPuesto(Puesto p) throws ObligatorioException { sTransito.agregarPuesto(p); }

    public List<Puesto> getPuestos() { return sTransito.getPuestos(); }

    public List<PuestoDTO> getPuestosDTO() { return sTransito.getPuestosDTO(); }

    public List<TarifaDTO> getTarifasPorPuesto(String nombrePuesto) throws ObligatorioException {
        return sTransito.getTarifasPorPuesto(nombrePuesto);
    }

    public ResultadoEmulacionDTO emularTransito(String matricula, String nombrePuesto, LocalDate fecha, LocalTime hora)
                throws ObligatorioException {
            ResultadoEmulacionDTO dto = sTransito.emularTransito(matricula, nombrePuesto, fecha, hora);
            System.out.println("🚗 Tránsito emulado - Avisando eventos: transitoRegistrado, saldoActualizado, notificacionesActualizadas");
            // El método interno ya genera notificaciones (saldo bajo / tránsito). Reflejamos eventos globales:
            avisar(Eventos.transitoRegistrado);
            avisar(Eventos.saldoActualizado);
            avisar(Eventos.notificacionesActualizadas);
            return dto;
        }
        /** Devuelve la lista de bonificaciones definidas en el sistema. */
    public List<obligatorio.obligatorio.Modelo.modelos.Bonificacion> getBonificacionesDefinidas() {
            return precargaDatos.getBonificaciones();
        }
                    /** Devuelve el propietario por cédula, o null si no existe. */
    public obligatorio.obligatorio.Modelo.modelos.Propietario getPropietarioPorCedula(String cedula) {
                if (cedula == null || cedula.isBlank()) return null;
                for (obligatorio.obligatorio.Modelo.modelos.Propietario p : sAcceso.getPropietarios()) {
                    if (cedula.equals(p.getCedula())) return p;
                }
                return null;
            }
}
