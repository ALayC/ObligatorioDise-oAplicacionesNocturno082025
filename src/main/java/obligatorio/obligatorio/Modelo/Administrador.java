package obligatorio.obligatorio.Modelo;

import java.util.Objects;

public final class Administrador {
    private final String cedula;
    private String password;
    private String nombreCompleto;

    public Administrador(String cedula, String password, String nombreCompleto) {
        this.cedula = Objects.requireNonNull(cedula);
        this.password = Objects.requireNonNull(password);
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto);
    }

    public String getCedula() { return cedula; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    @Override public boolean equals(Object o){return o instanceof Administrador a && cedula.equals(a.cedula);}
    @Override public int hashCode(){return Objects.hash(cedula);}
}
