package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Vehiculo {
    private final String matricula;
    private String modelo;
    private String color;
    private Categoria categoria;
    private Propietario propietario;

    // Tránsitos propios del vehículo
    private final List<Transito> transitos = new ArrayList<>();

    public Vehiculo(String matricula, String modelo, String color, Categoria categoria, Propietario propietario) {
        this.matricula = Objects.requireNonNull(matricula);
        this.modelo = Objects.requireNonNull(modelo);
        this.color = Objects.requireNonNull(color);
        this.categoria = Objects.requireNonNull(categoria);
        this.propietario = propietario;
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

    // --- Tránsitos del vehículo ---

    public void registrarTransito(Transito transito) {
        Objects.requireNonNull(transito, "transito no puede ser null");
        if (transito.getVehiculo() != this) {
            throw new IllegalArgumentException("El tránsito no pertenece a este vehículo");
        }
        transitos.add(transito);
    }

    public List<Transito> getTransitos() {
        return Collections.unmodifiableList(transitos);
    }

    public int getCantidadTransitos() {
        return transitos.size();
    }

    public BigDecimal getMontoTotalGastado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Transito t : transitos) {
            total = total.add(t.getMontoCobrado());
        }
        return total;
    }

    public List<Transito> getTransitosOrdenadosPorFechaDesc() {
        List<Transito> copia = new ArrayList<>(transitos);
        copia.sort(
                Comparator
                        .comparing(Transito::getFecha)
                        .thenComparing(Transito::getHora)
                        .reversed()
        );
        return copia;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Vehiculo v && matricula.equalsIgnoreCase(v.matricula);
    }

    @Override
    public int hashCode() {
        return matricula.toLowerCase().hashCode();
    }
}
