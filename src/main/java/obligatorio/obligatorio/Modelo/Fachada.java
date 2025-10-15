package obligatorio.obligatorio.Modelo;

import java.math.BigDecimal;
import java.util.List;

import obligatorio.obligatorio.Controladores.Respuesta;

public class Fachada {
    //================== Singleton ==================
    private static final Fachada INST = new Fachada();
    public static Fachada getInstancia() { return INST; }
    private Fachada(){}

    //================== Sistemas ===================
    private final SistemaAcceso   sAcceso   = new SistemaAcceso();
    private final SistemaTablero  sTablero  = new SistemaTablero();

    //================== Acceso =====================
    /** Alta por campos (crea la instancia y la registra) */
    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
                                   BigDecimal saldo, BigDecimal saldoMin, Estado estado)
            throws ObligatorioException {
        if (estado == null) estado = new Estado("Habilitado");
        Propietario p = new Propietario(cedula, pwd, nombreCompleto, saldo, saldoMin, estado);
        sAcceso.agregarPropietario(p);
    }

    /** Alta recibiendo la instancia (útil en precarga para NO duplicar objetos) */
    public void registrarPropietario(Propietario p) throws ObligatorioException {
        sAcceso.agregarPropietario(p);
    }

    public Propietario login(String cedula, String pwd) throws ObligatorioException {
        return sAcceso.login(cedula, pwd);
    }

    //============= Caso de uso: Tablero =============
    public List<Respuesta> armarRespuestasTablero(Propietario p) {
        return sTablero.armarRespuestasTablero(p);
    }

    public int borrarNotificaciones(Propietario p) {
        return sTablero.borrarNotificaciones(p);
    }
}
