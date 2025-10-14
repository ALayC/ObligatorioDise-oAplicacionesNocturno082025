package obligatorio.obligatorio.Modelo;

import java.util.Objects;

public final class Vehiculo {
    private final String matricula;
    private String modelo;
    private String color;
    private Categoria categoria;
    private Propietario propietario;

    public Vehiculo(String matricula, String modelo, String color, Categoria categoria, Propietario propietario) {
        this.matricula = Objects.requireNonNull(matricula);
        this.modelo = Objects.requireNonNull(modelo);
        this.color = Objects.requireNonNull(color);
        this.categoria = Objects.requireNonNull(categoria);
        this.propietario = Objects.requireNonNull(propietario);
    }

    public String getMatricula() { return matricula; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Propietario getPropietario() { return propietario; }
    public void setPropietario(Propietario propietario) { this.propietario = propietario; }

    @Override public boolean equals(Object o){return o instanceof Vehiculo v && matricula.equalsIgnoreCase(v.matricula);}
    @Override public int hashCode(){return matricula.toLowerCase().hashCode();}
}
