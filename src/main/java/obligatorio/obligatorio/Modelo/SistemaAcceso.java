package obligatorio.obligatorio.Modelo;

import java.util.ArrayList;
import java.util.List;

public class SistemaAcceso {
    private final List<Propietario> propietarios = new ArrayList<>();

    public void agregarPropietario(Propietario p) throws ObligatorioException {
        if (p == null) throw new ObligatorioException("Propietario nulo");
        if (p.getCedula() == null || p.getCedula().isBlank())
            throw new ObligatorioException("Cédula requerida");
        boolean existe = propietarios.stream()
                .anyMatch(x -> x.getCedula().equals(p.getCedula()));
        if (existe) throw new ObligatorioException("Ya existe un propietario con esa cédula");
        propietarios.add(p);
    }

    public Propietario login(String cedula, String pwd) throws ObligatorioException {
        for (Propietario p : propietarios) {
            if (p.getCedula().equals(cedula) && p.getPassword().equals(pwd)) {
                String nombreEstado = (p.getEstadoActual() != null) ? p.getEstadoActual().getNombre() : null;
                if (nombreEstado == null || !nombreEstado.equalsIgnoreCase("Habilitado")) {
                    throw new ObligatorioException("Usuario deshabilitado, no puede ingresar al sistema");
                }
                return p;
            }
        }
        throw new ObligatorioException("Acceso denegado");
    }
}
