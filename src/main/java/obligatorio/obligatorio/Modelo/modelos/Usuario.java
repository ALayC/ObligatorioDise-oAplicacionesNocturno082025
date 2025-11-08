package obligatorio.obligatorio.Modelo.modelos;

import java.util.Objects;

public abstract class Usuario {
    private final String cedula;
    private String password;
    private String nombreCompleto;

    public Usuario(String cedula, String password, String nombreCompleto) {
        this.cedula = Objects.requireNonNull(cedula);
        this.password = Objects.requireNonNull(password);
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto);
    }

    public String getCedula() { return cedula; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    @Override
    public String toString() { return nombreCompleto; }
}
