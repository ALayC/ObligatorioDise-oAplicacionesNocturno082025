package obligatorio.obligatorio.Modelo;

import java.math.BigDecimal;

public class Fachada {
    private static final Fachada INST = new Fachada();
    public static Fachada getInstancia() { return INST; }
    private Fachada(){}

    private final SistemaAcceso sAcceso = new SistemaAcceso();

    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
                                   BigDecimal saldo, BigDecimal saldoMin, Estado estado)
            throws ObligatorioException {
        if (estado == null) estado = new Estado("Habilitado");
        Propietario p = new Propietario(cedula, pwd, nombreCompleto, saldo, saldoMin, estado);
        sAcceso.agregarPropietario(p);
    }

    public Propietario login(String cedula, String pwd) throws ObligatorioException {
        return sAcceso.login(cedula, pwd);
    }
}
