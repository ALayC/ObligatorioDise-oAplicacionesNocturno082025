package obligatorio.obligatorio.Modelo.sistemas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import obligatorio.obligatorio.Modelo.modelos.Administrador;
import obligatorio.obligatorio.Modelo.modelos.EstadoPropietario;
import obligatorio.obligatorio.Modelo.modelos.FabricaEstadoPropietario;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Sesion;

public class SistemaAcceso {
    private final List<Propietario> propietarios = new ArrayList<>();
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Sesion> sesiones = new ArrayList<>();
    private final Set<String> administradoresLogueados = new HashSet<>();
    // Listas de tránsito y puestos movidas a SistemaTransito.

    public void agregarPropietario(Propietario p) throws ObligatorioException {
        if (p == null)
            throw new ObligatorioException("Propietario nulo");
        if (p.getCedula() == null || p.getCedula().isBlank())
            throw new ObligatorioException("Cédula requerida");
        boolean existe = propietarios.stream().anyMatch(x -> x.getCedula().equals(p.getCedula()));
        if (existe)
            throw new ObligatorioException("Ya existe un propietario con esa cédula");
        propietarios.add(p);
    }

    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
            BigDecimal saldo, BigDecimal saldoMin, String nombreEstado)
            throws ObligatorioException {

        Propietario p = new Propietario(cedula, pwd, nombreCompleto, saldo, saldoMin, null);
        EstadoPropietario estado = FabricaEstadoPropietario.crearEstado(nombreEstado, p);
        p.setEstadoPropietario(estado);

        agregarPropietario(p);
    }

    public void agregarAdministrador(Administrador a) throws ObligatorioException {
        if (a == null)
            throw new ObligatorioException("Administrador nulo");
        if (a.getCedula() == null || a.getCedula().isBlank())
            throw new ObligatorioException("Cédula requerida");
        boolean existe = administradores.stream().anyMatch(x -> x.getCedula().equals(a.getCedula()));
        if (existe)
            throw new ObligatorioException("Ya existe un administrador con esa cédula");
        administradores.add(a);
    }

    public void agregarAdministrador(String cedula, String pwd, String nombreCompleto) throws ObligatorioException {
        agregarAdministrador(new Administrador(cedula, pwd, nombreCompleto));
    }

    public Sesion loginPropietario(String cedula, String pwd) throws ObligatorioException {
        for (Propietario p : propietarios) {
            if (p.getCedula().equals(cedula) && p.getPassword().equals(pwd)) {
                String nombreEstado = p.getEstadoPropietario() != null ? p.getEstadoPropietario().getNombre() : null;
                if (nombreEstado == null || !nombreEstado.equalsIgnoreCase("Habilitado"))
                    throw new ObligatorioException("Usuario deshabilitado, no puede ingresar al sistema");
                Sesion s = new Sesion(p);
                sesiones.add(s);
                return s;
            }
        }
        throw new ObligatorioException("Acceso denegado");
    }

    public Administrador loginAdministrador(String cedula, String pwd) throws ObligatorioException {
        for (Administrador a : administradores) {
            if (a.getCedula().equals(cedula) && a.getPassword().equals(pwd)) {
                if (administradoresLogueados.contains(cedula))
                    throw new ObligatorioException("Ud. ya está logueado");
                administradoresLogueados.add(cedula);
                return a;
            }
        }
        throw new ObligatorioException("Login incorrecto");
    }

    public List<Sesion> getSesiones() {
        return sesiones;
    }

    public void logout(Sesion s) {
        sesiones.remove(s);
    }

    public void logoutAdministrador(String cedula) {
        if (cedula != null)
            administradoresLogueados.remove(cedula);
    }

    /** Exponer propietarios para otros sistemas (p.ej. SistemaTransito). */
    public List<Propietario> getPropietarios() {
        return propietarios;
    }

    /** Recargar saldo de un propietario (método antes referido por la Fachada). */
    public void recargarSaldo(Propietario p, BigDecimal monto) throws ObligatorioException {
        if (p == null)
            throw new ObligatorioException("Propietario nulo");
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new ObligatorioException("Monto inválido");
        p.setSaldoActual(p.getSaldoActual().add(monto));
        // Podría añadirse notificación de recarga aquí si el CU lo requiere.
    }
}
