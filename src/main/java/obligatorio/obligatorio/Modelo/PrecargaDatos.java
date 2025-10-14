package obligatorio.obligatorio.Modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PrecargaDatos {
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Propietario> propietarios = new ArrayList<>();
    private final List<Puesto> puestos = new ArrayList<>();
    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Tarifa> tarifas = new ArrayList<>();
    private final List<Vehiculo> vehiculos = new ArrayList<>();
    private final List<Bonificacion> bonificaciones = new ArrayList<>();
    private final List<Estado> estados = new ArrayList<>();
    private final List<AsignacionBonificacion> asignaciones = new ArrayList<>();

    public static PrecargaDatos crear() {
        PrecargaDatos d = new PrecargaDatos();
        d.cargar();
        return d;
    }

    public List<Administrador> getAdministradores() { return administradores; }
    public List<Propietario> getPropietarios() { return propietarios; }
    public List<Puesto> getPuestos() { return puestos; }
    public List<Categoria> getCategorias() { return categorias; }
    public List<Tarifa> getTarifas() { return tarifas; }
    public List<Vehiculo> getVehiculos() { return vehiculos; }
    public List<Bonificacion> getBonificaciones() { return bonificaciones; }
    public List<Estado> getEstados() { return estados; }
    public List<AsignacionBonificacion> getAsignaciones() { return asignaciones; }

    private void cargar() {
        Estado habilitado = new Estado("Habilitado");
        Estado deshabilitado = new Estado("Deshabilitado");
        Estado suspendido = new Estado("Suspendido");
        Estado penalizado = new Estado("Penalizado");
        estados.add(habilitado);
        estados.add(deshabilitado);
        estados.add(suspendido);
        estados.add(penalizado);

        Administrador admin1 = new Administrador("12345678", "admin.123", "Usuario Administrador");
        Administrador admin2 = new Administrador("87654321", "root.123", "Admin Secundario");
        administradores.add(admin1);
        administradores.add(admin2);

        Propietario prop1 = new Propietario("23456789", "prop.123", "Usuario Propietario", new BigDecimal("2000"), new BigDecimal("500"), habilitado);
        Propietario prop2 = new Propietario("34567890", "prop.456", "Propietario Dos", new BigDecimal("800"), new BigDecimal("200"), habilitado);
        propietarios.add(prop1);
        propietarios.add(prop2);

        Categoria auto = new Categoria("Automóvil");
        Categoria camion = new Categoria("Camión");
        Categoria moto = new Categoria("Moto");
        categorias.add(auto);
        categorias.add(camion);
        categorias.add(moto);

        Bonificacion exonerados = new Bonificacion("Exonerados");
        Bonificacion frecuentes = new Bonificacion("Frecuentes");
        Bonificacion trabajadores = new Bonificacion("Trabajadores");
        bonificaciones.add(exonerados);
        bonificaciones.add(frecuentes);
        bonificaciones.add(trabajadores);

        Puesto p1 = new Puesto("Peaje Santa Lucía", "Ruta 5 KM 56");
        Puesto p2 = new Puesto("Peaje Pando", "Ruta Interbalnearia KM 32");
        puestos.add(p1);
        puestos.add(p2);

        Tarifa t11 = new Tarifa(p1, auto, new BigDecimal("120"));
        Tarifa t12 = new Tarifa(p1, camion, new BigDecimal("300"));
        Tarifa t13 = new Tarifa(p1, moto, new BigDecimal("80"));
        Tarifa t21 = new Tarifa(p2, auto, new BigDecimal("100"));
        Tarifa t22 = new Tarifa(p2, camion, new BigDecimal("270"));
        Tarifa t23 = new Tarifa(p2, moto, new BigDecimal("70"));
        p1.agregarTarifa(t11);
        p1.agregarTarifa(t12);
        p1.agregarTarifa(t13);
        p2.agregarTarifa(t21);
        p2.agregarTarifa(t22);
        p2.agregarTarifa(t23);
        tarifas.add(t11); tarifas.add(t12); tarifas.add(t13);
        tarifas.add(t21); tarifas.add(t22); tarifas.add(t23);

        Vehiculo v1 = new Vehiculo("SBA1234", "Sedan", "Rojo", auto, prop1);
        Vehiculo v2 = new Vehiculo("SBC5678", "Camión 3/4", "Azul", camion, prop1);
        Vehiculo v3 = new Vehiculo("SBD9012", "Street", "Negro", moto, prop2);
        prop1.agregarVehiculo(v1);
        prop1.agregarVehiculo(v2);
        prop2.agregarVehiculo(v3);
        vehiculos.add(v1);
        vehiculos.add(v2);
        vehiculos.add(v3);

        AsignacionBonificacion a1 = new AsignacionBonificacion(prop1, p1, frecuentes, LocalDate.now().minusDays(5));
        AsignacionBonificacion a2 = new AsignacionBonificacion(prop1, p2, exonerados, LocalDate.now().minusDays(2));
        AsignacionBonificacion a3 = new AsignacionBonificacion(prop2, p1, trabajadores, LocalDate.now().minusDays(1));
        prop1.agregarAsignacion(a1);
        prop1.agregarAsignacion(a2);
        prop2.agregarAsignacion(a3);
        asignaciones.add(a1);
        asignaciones.add(a2);
        asignaciones.add(a3);
    }
}
