package obligatorio.obligatorio.Modelo;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import obligatorio.obligatorio.Controladores.Respuesta;

public class Fachada {
    // ================== Singleton ==================
    private static final Fachada INST = new Fachada();

    public static Fachada getInstancia() {
        return INST;
    }

    private Fachada() {
    }

    // ================== Sistemas ===================
    private final SistemaAcceso sAcceso = new SistemaAcceso();
    private final SistemaTablero sTablero = new SistemaTablero();

    // ================= Datos de dominio (Tránsitos) =================
    private final List<Transito> transitos = new ArrayList<>();

    public void registrarTransito(Transito t) {
        if (t != null)
            this.transitos.add(t);
    }

    public List<Transito> getTransitos() {
        return this.transitos;
    }

    // ================== Acceso =====================
    /** Alta por campos (crea la instancia y la registra) */
    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
            BigDecimal saldo, BigDecimal saldoMin, Estado estado)
            throws ObligatorioException {
        if (estado == null)
            estado = new Estado("Habilitado");
        Propietario p = new Propietario(cedula, pwd, nombreCompleto, saldo, saldoMin, estado);
        sAcceso.agregarPropietario(p);
    }

    // ============= Operaciones de Propietario =============
    public void recargarSaldo(Propietario p, BigDecimal monto) throws ObligatorioException {
        if (p == null) {
            throw new ObligatorioException("Sesión expirada");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ObligatorioException("El monto debe ser mayor que cero");
        }

        // Actualiza saldo
        p.setSaldoActual(p.getSaldoActual().add(monto));

        // Genera notificación
        p.agregarNotificacion(
                new Notificacion("Recarga acreditada: $" + monto, LocalDateTime.now()));
    }

    /** Alta recibiendo la instancia (útil en precarga para NO duplicar objetos) */
    public void registrarPropietario(Propietario p) throws ObligatorioException {
        sAcceso.agregarPropietario(p);
    }

    public Propietario login(String cedula, String pwd) throws ObligatorioException {
        return sAcceso.login(cedula, pwd);
    }

    // ============= Caso de uso: Tablero =============
    public List<Respuesta> armarRespuestasTablero(Propietario p) {
        return sTablero.armarRespuestasTablero(p);
    }

    public int borrarNotificaciones(Propietario p) {
        return sTablero.borrarNotificaciones(p);
    }
}
