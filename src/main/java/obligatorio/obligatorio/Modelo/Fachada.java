package obligatorio.obligatorio.Modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import obligatorio.obligatorio.Controladores.Respuesta;
import obligatorio.obligatorio.DTO.ResultadoEmulacionDTO;

public class Fachada {
    private static final Fachada INST = new Fachada();
    public static Fachada getInstancia() { return INST; }
    private Fachada() {}

    private final SistemaAcceso sAcceso = new SistemaAcceso();
    private final SistemaTablero sTablero = new SistemaTablero();

    public void registrarTransito(Transito t) { sAcceso.registrarTransito(t); }
    public List<Transito> getTransitos() { return sAcceso.getTransitos(); }

    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
                                   BigDecimal saldo, BigDecimal saldoMin, Estado estado)
            throws ObligatorioException { sAcceso.agregarPropietario(cedula, pwd, nombreCompleto, saldo, saldoMin, estado); }

    public void registrarPropietario(Propietario p) throws ObligatorioException { sAcceso.agregarPropietario(p); }

    public void agregarAdministrador(String cedula, String pwd, String nombreCompleto)
            throws ObligatorioException { sAcceso.agregarAdministrador(cedula, pwd, nombreCompleto); }

    public Sesion loginPropietario(String cedula, String pwd) throws ObligatorioException { return sAcceso.loginPropietario(cedula, pwd); }

    public Administrador loginAdministrador(String cedula, String pwd) throws ObligatorioException { return sAcceso.loginAdministrador(cedula, pwd); }

    public List<Sesion> getSesiones() { return sAcceso.getSesiones(); }
    public void logout(Sesion s) { sAcceso.logout(s); }
    public void logoutAdministrador(String cedula) { sAcceso.logoutAdministrador(cedula); }
    public void recargarSaldo(Propietario p, BigDecimal monto) throws ObligatorioException { sAcceso.recargarSaldo(p, monto); }

    public List<Respuesta> armarRespuestasTablero(Propietario p) { return sTablero.armarRespuestasTablero(p); }
    public int borrarNotificaciones(Propietario p) { return sTablero.borrarNotificaciones(p); }

    // Métodos para emular tránsito
    public void agregarPuesto(Puesto p) throws ObligatorioException { sAcceso.agregarPuesto(p); }
    public List<Puesto> getPuestos() { return sAcceso.getPuestos(); }
    public ResultadoEmulacionDTO emularTransito(String matricula, String nombrePuesto, LocalDateTime fechaHora) 
            throws ObligatorioException { 
        return sAcceso.emularTransito(matricula, nombrePuesto, fechaHora); 
    }
}
