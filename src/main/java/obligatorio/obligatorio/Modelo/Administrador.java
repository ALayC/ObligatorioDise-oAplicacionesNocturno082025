package obligatorio.obligatorio.Modelo;

import java.util.Objects;

public final class Administrador extends Usuario {
    public Administrador(String cedula, String password, String nombreCompleto) {
        super(cedula, password, nombreCompleto);
    }

    @Override
    public boolean equals(Object o){ return o instanceof Administrador a && getCedula().equals(a.getCedula()); }
    @Override
    public int hashCode(){ return Objects.hash(getCedula()); }
}
