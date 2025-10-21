package obligatorio.obligatorio.Modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // Solo para referencia/depuración, la fachada es la fuente de verdad de
        // tránsitos
        private final List<Notificacion> notificaciones = new ArrayList<>();
        private final List<Transito> transitos = new ArrayList<>();

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

        public List<Estado> getEstados() {
                return estados;
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

        private void cargar() throws ObligatorioException {
                Fachada f = Fachada.getInstancia();

                // ----- Estados -----
                Estado habilitado = new Estado("Habilitado");
                Estado deshabilitado = new Estado("Deshabilitado");
                Estado suspendido = new Estado("Suspendido");
                Estado penalizado = new Estado("Penalizado");
                estados.add(habilitado);
                estados.add(deshabilitado);
                estados.add(suspendido);
                estados.add(penalizado);

                // ----- Administradores -----
                Administrador admin1 = new Administrador("12345678", "admin.123", "Usuario Administrador");
                Administrador admin2 = new Administrador("87654321", "root.123", "Admin Secundario");
                administradores.add(admin1);
                administradores.add(admin2);

                // ----- Propietarios -----
                Propietario prop1 = new Propietario(
                                "1", "1", "Walter",
                                new BigDecimal("2000.00"), new BigDecimal("500.00"), habilitado);

                Propietario prop2 = new Propietario(
                                "34567890", "prop.456", "Propietario Dos",
                                new BigDecimal("800.00"), new BigDecimal("200.00"), habilitado);

                propietarios.add(prop1);
                propietarios.add(prop2);

                // Registrar propietarios en el sistema de acceso a través de la fachada
                f.registrarPropietario(prop1);
                f.registrarPropietario(prop2);

                // ----- Categorías -----
                Categoria auto = new Categoria("Automóvil");
                Categoria camion = new Categoria("Camión");
                Categoria moto = new Categoria("Moto");
                categorias.add(auto);
                categorias.add(camion);
                categorias.add(moto);

                // ----- Bonificaciones -----
                Bonificacion exonerados = new Bonificacion("Exonerados");
                Bonificacion frecuentes = new Bonificacion("Frecuentes");
                Bonificacion trabajadores = new Bonificacion("Trabajadores");
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

                // ----- Vehículos -----
                Vehiculo v1 = new Vehiculo("SBA1234", "Sedan", "Rojo", auto, prop1);
                Vehiculo v2 = new Vehiculo("SBC5678", "Camión 3/4", "Azul", camion, prop1);
                Vehiculo v3 = new Vehiculo("SBD9012", "Street", "Negro", moto, prop2);

                prop1.agregarVehiculo(v1);
                prop1.agregarVehiculo(v2);
                prop2.agregarVehiculo(v3);

                vehiculos.add(v1);
                vehiculos.add(v2);
                vehiculos.add(v3);

                // ----- Asignaciones de bonificación -----
                AsignacionBonificacion a1 = new AsignacionBonificacion(prop1, p1, frecuentes,
                                LocalDate.now().minusDays(5));
                AsignacionBonificacion a2 = new AsignacionBonificacion(prop1, p2, exonerados,
                                LocalDate.now().minusDays(2));
                AsignacionBonificacion a3 = new AsignacionBonificacion(prop2, p1, trabajadores,
                                LocalDate.now().minusDays(1));

                prop1.agregarAsignacion(a1);
                prop1.agregarAsignacion(a2);
                prop2.agregarAsignacion(a3);

                asignaciones.add(a1);
                asignaciones.add(a2);
                asignaciones.add(a3);

                // ----- Notificaciones -----
                Notificacion n11 = new Notificacion("Recarga acreditada: $500", LocalDateTime.now().minusHours(2));
                Notificacion n12 = new Notificacion("Saldo bajo: recuerde recargar",
                                LocalDateTime.now().minusDays(1).withHour(9).withMinute(30));
                prop1.agregarNotificacion(n11);
                prop1.agregarNotificacion(n12);
                notificaciones.add(n11);
                notificaciones.add(n12);

                Notificacion n21 = new Notificacion("Bonificación 'Trabajadores' aplicada en Peaje Santa Lucía",
                                LocalDateTime.now().minusHours(5));
                prop2.agregarNotificacion(n21);
                notificaciones.add(n21);

                // ----- Tránsitos (coherentes con tarifas/bonificaciones) -----
                // v1 (auto) por p1 (tarifa 120) con bonificación FRECUENTES => cobra 100
                BigDecimal base_v1_p1 = t11.getMonto(); // 120.00
                BigDecimal bonif_v1_p1 = new BigDecimal("20.00"); // ejemplo
                BigDecimal cobrado_v1_p1 = base_v1_p1.subtract(bonif_v1_p1); // 100.00

                Transito tr1 = new Transito(
                                v1, p1, t11,
                                LocalDateTime.now().minusHours(3),
                                base_v1_p1,
                                cobrado_v1_p1,
                                frecuentes,
                                true);

                // v2 (camión) por p2 (tarifa 270) con EXONERADOS => cobra 0
                BigDecimal base_v2_p2 = t22.getMonto(); // 270.00
                Transito tr2 = new Transito(
                                v2, p2, t22,
                                LocalDateTime.now().minusDays(1).withHour(18).withMinute(15),
                                base_v2_p2,
                                BigDecimal.ZERO,
                                exonerados,
                                true);

                // v3 (moto) por p1 (tarifa 80) sin bonificación => cobra 80
                BigDecimal base_v3_p1 = t13.getMonto(); // 80.00
                Transito tr3 = new Transito(
                                v3, p1, t13,
                                LocalDateTime.now().minusDays(2).withHour(8).withMinute(5),
                                base_v3_p1,
                                base_v3_p1,
                                null,
                                true);

                // registrar en fachada
                f.registrarTransito(tr1);
                f.registrarTransito(tr2);
                f.registrarTransito(tr3);

                // mantener en la lista local
                transitos.add(tr1);
                transitos.add(tr2);
                transitos.add(tr3);
        }
}
