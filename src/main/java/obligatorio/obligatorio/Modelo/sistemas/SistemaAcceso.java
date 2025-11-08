package obligatorio.obligatorio.Modelo.sistemas;

import obligatorio.obligatorio.Modelo.modelos.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SistemaAcceso {
    private final List<Propietario> propietarios = new ArrayList<>();
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Sesion> sesiones = new ArrayList<>();
    private final Set<String> administradoresLogueados = new HashSet<>();
    private final List<Transito> transitos = new ArrayList<>();
    private final List<Puesto> puestos = new ArrayList<>();

    public void agregarPropietario(Propietario p) throws ObligatorioException {
        if (p == null) throw new ObligatorioException("Propietario nulo");
        if (p.getCedula() == null || p.getCedula().isBlank()) throw new ObligatorioException("Cédula requerida");
        boolean existe = propietarios.stream().anyMatch(x -> x.getCedula().equals(p.getCedula()));
        if (existe) throw new ObligatorioException("Ya existe un propietario con esa cédula");
        propietarios.add(p);
    }

    public void agregarPropietario(String cedula, String pwd, String nombreCompleto,
                                   BigDecimal saldo, BigDecimal saldoMin, Estado estado) throws ObligatorioException {
        if (estado == null) estado = new Estado("Habilitado");
        agregarPropietario(new Propietario(cedula, pwd, nombreCompleto, saldo, saldoMin, estado));
    }

    public void agregarAdministrador(Administrador a) throws ObligatorioException {
        if (a == null) throw new ObligatorioException("Administrador nulo");
        if (a.getCedula() == null || a.getCedula().isBlank()) throw new ObligatorioException("Cédula requerida");
        boolean existe = administradores.stream().anyMatch(x -> x.getCedula().equals(a.getCedula()));
        if (existe) throw new ObligatorioException("Ya existe un administrador con esa cédula");
        administradores.add(a);
    }

    public void agregarAdministrador(String cedula, String pwd, String nombreCompleto) throws ObligatorioException {
        agregarAdministrador(new Administrador(cedula, pwd, nombreCompleto));
    }

    public Sesion loginPropietario(String cedula, String pwd) throws ObligatorioException {
        for (Propietario p : propietarios) {
            if (p.getCedula().equals(cedula) && p.getPassword().equals(pwd)) {
                String nombreEstado = p.getEstadoActual() != null ? p.getEstadoActual().getNombre() : null;
                if (nombreEstado == null || !nombreEstado.equalsIgnoreCase("Habilitado"))
                    throw new ObligatorioException("Usuario deshabilitado, no puede ingresar al sistema");
                Sesion s = new Sesion(p);
                sesiones.add(s);
                return s;
            }
        }
        throw new ObligatorioException("Acceso denegado");
    }

    public Administrador loginAdministrador(String cedula, String pwd) throws ObligatorioException {
        for (Administrador a : administradores) {
            if (a.getCedula().equals(cedula) && a.getPassword().equals(pwd)) {
                if (administradoresLogueados.contains(cedula))
                    throw new ObligatorioException("Ud. ya está logueado");
                administradoresLogueados.add(cedula);
                return a;
            }
        }
        throw new ObligatorioException("Login incorrecto");
    }

    public List<Sesion> getSesiones() { return sesiones; }
    public void logout(Sesion s) { sesiones.remove(s); }
    public void logoutAdministrador(String cedula) { if (cedula != null) administradoresLogueados.remove(cedula); }

    public void recargarSaldo(Propietario p, BigDecimal monto) throws ObligatorioException {
        if (p == null) throw new ObligatorioException("Sesión expirada");
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new ObligatorioException("El monto debe ser mayor que cero");
        p.setSaldoActual(p.getSaldoActual().add(monto));
        p.agregarNotificacion(new Notificacion("Recarga acreditada: $" + monto, LocalDateTime.now()));
    }

    public void registrarTransito(Transito t) { if (t != null) transitos.add(t); }
    public List<Transito> getTransitos() { return transitos; }

    public void agregarPuesto(Puesto p) throws ObligatorioException {
        if (p == null) throw new ObligatorioException("Puesto nulo");
        if (puestos.contains(p)) throw new ObligatorioException("El puesto ya existe");
        puestos.add(p);
    }

    public List<Puesto> getPuestos() { return puestos; }

    /**
     * Emula un tránsito de un vehículo por un puesto.
     * 
     * @param matricula    Matrícula del vehículo
     * @param nombrePuesto Nombre del puesto
     * @param fechaHora    Fecha y hora del tránsito
     * @return ResultadoEmulacionDTO con información del tránsito
     * @throws ObligatorioException si hay errores de validación
     */
    public obligatorio.obligatorio.DTO.ResultadoEmulacionDTO emularTransito(String matricula, String nombrePuesto, LocalDateTime fechaHora) 
            throws ObligatorioException {
        
        // Validar parámetros
        if (matricula == null || matricula.isBlank()) 
            throw new ObligatorioException("Matrícula requerida");
        if (nombrePuesto == null || nombrePuesto.isBlank()) 
            throw new ObligatorioException("Puesto requerido");
        if (fechaHora == null) 
            throw new ObligatorioException("Fecha y hora requeridas");

        // Buscar vehículo
        Vehiculo vehiculo = null;
        for (Propietario p : propietarios) {
            for (Vehiculo v : p.getVehiculos()) {
                if (v.getMatricula().equalsIgnoreCase(matricula)) {
                    vehiculo = v;
                    break;
                }
            }
            if (vehiculo != null) break;
        }
        
        if (vehiculo == null) 
            throw new ObligatorioException("No existe el vehículo");

        Propietario propietario = vehiculo.getPropietario();

        // Validar estado del propietario
        String nombreEstado = propietario.getEstadoActual() != null ? 
            propietario.getEstadoActual().getNombre() : null;
        
        if (nombreEstado != null) {
            if (nombreEstado.equalsIgnoreCase("Deshabilitado")) {
                throw new ObligatorioException("El propietario del vehículo está deshabilitado, no puede realizar tránsitos");
            }
            if (nombreEstado.equalsIgnoreCase("Suspendido")) {
                throw new ObligatorioException("El propietario del vehículo está suspendido, no puede realizar tránsitos");
            }
        }

        // Buscar puesto
        Puesto puesto = null;
        for (Puesto p : puestos) {
            if (p.getNombre().equalsIgnoreCase(nombrePuesto)) {
                puesto = p;
                break;
            }
        }
        
        if (puesto == null) 
            throw new ObligatorioException("Puesto no encontrado");

        // Buscar tarifa correspondiente
        Categoria categoria = vehiculo.getCategoria();
        Tarifa tarifa = null;
        for (Tarifa t : puesto.getTarifas()) {
            if (t.getCategoria().equals(categoria)) {
                tarifa = t;
                break;
            }
        }
        
        if (tarifa == null) 
            throw new ObligatorioException("No existe tarifa para la categoría del vehículo en este puesto");

        // Determinar si hay bonificación
        boolean esPenalizado = nombreEstado != null && nombreEstado.equalsIgnoreCase("Penalizado");
        Bonificacion bonificacionAplicada = null;
        
        if (!esPenalizado) {
            // Buscar bonificación del propietario para este puesto
            for (AsignacionBonificacion asig : propietario.getAsignaciones()) {
                if (asig.getPuesto().equals(puesto)) {
                    bonificacionAplicada = asig.getBonificacion();
                    break;
                }
            }
        }

        // Calcular monto
        BigDecimal montoBase = tarifa.getMonto();
        BigDecimal montoCobrado = montoBase;

        // Aplicar bonificación si existe
        if (bonificacionAplicada != null) {
            montoCobrado = bonificacionAplicada.calcularMonto(montoBase);
        }

        // Verificar saldo suficiente
        if (propietario.getSaldoActual().compareTo(montoCobrado) < 0) {
            throw new ObligatorioException("Saldo insuficiente: " + propietario.getSaldoActual());
        }

        // Descontar saldo
        propietario.setSaldoActual(propietario.getSaldoActual().subtract(montoCobrado));

        // Registrar tránsito
        Transito transito = new Transito(vehiculo, puesto, tarifa, fechaHora, 
                                        montoBase, montoCobrado, bonificacionAplicada, true);
        transitos.add(transito);

        // Notificación de tránsito (si no es penalizado)
        if (!esPenalizado) {
            String mensaje = fechaHora.toString() + " Pasaste por el puesto " + puesto.getNombre() + 
                           " con el vehículo " + vehiculo.getMatricula();
            propietario.agregarNotificacion(new Notificacion(mensaje, LocalDateTime.now()));
        }

        // Notificación de saldo bajo si corresponde
        if (propietario.getSaldoActual().compareTo(propietario.getSaldoMinimoAlerta()) < 0) {
            String mensaje = LocalDateTime.now().toString() + " Tu saldo actual es de $ " + 
                           propietario.getSaldoActual() + " Te recomendamos hacer una recarga";
            propietario.agregarNotificacion(new Notificacion(mensaje, LocalDateTime.now()));
        }

        // Crear DTO de resultado
        obligatorio.obligatorio.DTO.ResultadoEmulacionDTO resultado = 
            new obligatorio.obligatorio.DTO.ResultadoEmulacionDTO();
        resultado.setNombrePropietario(propietario.getNombreCompleto());
        resultado.setEstado(propietario.getEstadoActual().getNombre());
        resultado.setCategoria(categoria.getNombre());
        resultado.setBonificacion(bonificacionAplicada != null ? bonificacionAplicada.getNombre() : null);
        resultado.setCostoTransito(montoCobrado);
        resultado.setSaldoDespues(propietario.getSaldoActual());

        return resultado;
    }
}
