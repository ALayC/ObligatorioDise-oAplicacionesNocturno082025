package obligatorio.obligatorio.Modelo.modelos;

import java.math.BigDecimal;
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
        private final List<AsignacionBonificacion> asignaciones = new ArrayList<>();
        private final List<Notificacion> notificaciones = new ArrayList<>();
        private final List<Transito> transitos = new ArrayList<>();

        // Estados de propietarios definidos en el sistema (según enunciado)
        private final List<String> estadosPropietario = List.of(
                        "Habilitado",
                        "Deshabilitado",
                        "Suspendido",
                        "Penalizado");

        public static PrecargaDatos crear() throws ObligatorioException {
                PrecargaDatos d = new PrecargaDatos();
                d.cargar();
                return d;
        }

        public List<Administrador> getAdministradores() {
                return administradores;
        }

        public List<Propietario> getPropietarios() {
                return propietarios;
        }

        public List<Puesto> getPuestos() {
                return puestos;
        }

        public List<Categoria> getCategorias() {
                return categorias;
        }

        public List<Tarifa> getTarifas() {
                return tarifas;
        }

        public List<Vehiculo> getVehiculos() {
                return vehiculos;
        }

        public List<Bonificacion> getBonificaciones() {
                return bonificaciones;
        }

        public List<AsignacionBonificacion> getAsignaciones() {
                return asignaciones;
        }

        public List<Notificacion> getNotificaciones() {
                return notificaciones;
        }

        public List<Transito> getTransitos() {
                return transitos;
        }

        // Nueva: estados de propietarios definidos
        public List<String> getEstadosPropietario() {
                return estadosPropietario;
        }

        private void cargar() throws ObligatorioException {
                // ----- Administradores -----
                Administrador admin1 = new Administrador("a", "a", "Usuario Administrador");
                Administrador admin2 = new Administrador("87654321", "root.123", "Admin Secundario");
                administradores.add(admin1);
                administradores.add(admin2);

                // ----- Propietarios -----
                Propietario prop1 = new Propietario("1", "1", "Walter",
                                new BigDecimal("2000.00"), new BigDecimal("500.00"));

                Propietario prop2 = new Propietario("2", "2", "josé",
                                new BigDecimal("800.00"), new BigDecimal("200.00"));

                propietarios.add(prop1);
                propietarios.add(prop2);

                // ----- Categorías -----
                Categoria auto = new Categoria("Automóvil");
                Categoria camion = new Categoria("Camión");
                Categoria moto = new Categoria("Moto");
                categorias.add(auto);
                categorias.add(camion);
                categorias.add(moto);

                // ----- Bonificaciones -----
                Bonificacion exonerados = new BonificacionExonerados();
                Bonificacion frecuentes = new BonificacionFrecuentes();
                Bonificacion trabajadores = new BonificacionTrabajadores();

                bonificaciones.add(exonerados);
                bonificaciones.add(frecuentes);
                bonificaciones.add(trabajadores);
                // ----- Puestos -----
                Puesto p1 = new Puesto("Peaje Santa Lucía", "Ruta 5 KM 56");
                Puesto p2 = new Puesto("Peaje Pando", "Ruta Interbalnearia KM 32");
                puestos.add(p1);
                puestos.add(p2);

                // ----- Tarifas -----
                Tarifa t11 = new Tarifa(p1, auto, new BigDecimal("120.00"));
                Tarifa t12 = new Tarifa(p1, camion, new BigDecimal("300.00"));
                Tarifa t13 = new Tarifa(p1, moto, new BigDecimal("80.00"));

                Tarifa t21 = new Tarifa(p2, auto, new BigDecimal("100.00"));
                Tarifa t22 = new Tarifa(p2, camion, new BigDecimal("270.00"));
                Tarifa t23 = new Tarifa(p2, moto, new BigDecimal("70.00"));

                p1.agregarTarifa(t11);
                p1.agregarTarifa(t12);
                p1.agregarTarifa(t13);
                p2.agregarTarifa(t21);
                p2.agregarTarifa(t22);
                p2.agregarTarifa(t23);

                tarifas.add(t11);
                tarifas.add(t12);
                tarifas.add(t13);
                tarifas.add(t21);
                tarifas.add(t22);
                tarifas.add(t23);

                // ----- Vehículos (asociados a propietarios) -----
                Vehiculo v1 = new Vehiculo("1", "1", "Rojo", auto, prop1);
                Vehiculo v2 = new Vehiculo("SBC5678", "Camión 3/4", "Azul", camion, prop1);
                Vehiculo v3 = new Vehiculo("SBD9012", "Street", "Negro", moto, prop1);
                Vehiculo v4 = new Vehiculo("2", "2", "Rojo", auto, prop2);

                prop1.agregarVehiculo(v1);
                prop1.agregarVehiculo(v2);
                prop1.agregarVehiculo(v3);
                prop2.agregarVehiculo(v4);

                vehiculos.add(v1);
                vehiculos.add(v2);
                vehiculos.add(v3);
                vehiculos.add(v4);

                // ----- Asignaciones de bonificación -----
                AsignacionBonificacion a1 = new AsignacionBonificacion(prop1, p1, frecuentes,
                                java.time.LocalDate.now().minusDays(5));
                AsignacionBonificacion a3 = new AsignacionBonificacion(prop2, p1, trabajadores,
                                java.time.LocalDate.now().minusDays(1));

                prop1.agregarAsignacion(a1);
                prop2.agregarAsignacion(a3);

                asignaciones.add(a1);
                asignaciones.add(a3);

                // ----- Notificaciones -----
                Notificacion n11 = new Notificacion(
                                java.time.LocalDate.now().minusDays(2) + " Recarga acreditada: $500",
                                java.time.LocalDate.now().minusDays(2));
                Notificacion n12 = new Notificacion(
                                java.time.LocalDate.now().minusDays(1) + " Saldo bajo: recuerde recargar",
                                java.time.LocalDate.now().minusDays(1));
                prop1.agregarNotificacion(n11);
                prop1.agregarNotificacion(n12);
                notificaciones.add(n11);
                notificaciones.add(n12);

                Notificacion n21 = new Notificacion(
                                java.time.LocalDate.now().minusDays(5)
                                                + " Bonificación 'Trabajadores' aplicada en Peaje Santa Lucía",
                                java.time.LocalDate.now().minusDays(5));
                prop2.agregarNotificacion(n21);
                notificaciones.add(n21);
        }
}
