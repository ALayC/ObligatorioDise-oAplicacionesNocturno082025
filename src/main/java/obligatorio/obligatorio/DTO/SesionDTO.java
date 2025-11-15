package obligatorio.obligatorio.DTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Sesion;

/**
 * DTO para exponer información de una sesión al frontend.
 * Evita enviar la entidad completa y controla exactamente los campos mostrados.
 */
public class SesionDTO {
    private String fechaIngreso;
    private String cedulaPropietario;
    private String nombrePropietario;
    private String estadoPropietario;
    private String saldoActual;
    private int cantidadVehiculos;
    
    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public SesionDTO(Sesion s) {
    this.fechaIngreso = FMT.format(s.getFechaIngreso());
        Propietario p = s.getPropietario();
        this.cedulaPropietario = p.getCedula();
        this.nombrePropietario = p.getNombreCompleto();
        this.estadoPropietario = p.getEstadoPropietario() != null ? p.getEstadoPropietario().getNombre() : null;
        this.saldoActual = p.getSaldoActual() != null ? p.getSaldoActual().toPlainString() : null;
        this.cantidadVehiculos = p.getVehiculos().size();
    }

    public String getFechaIngreso() { return fechaIngreso; }
    public String getCedulaPropietario() { return cedulaPropietario; }
    public String getNombrePropietario() { return nombrePropietario; }
    public String getEstadoPropietario() { return estadoPropietario; }
    public String getSaldoActual() { return saldoActual; }
    public int getCantidadVehiculos() { return cantidadVehiculos; }

    public static List<SesionDTO> lista(List<Sesion> sesiones){
        List<SesionDTO> lista = new ArrayList<>();
        for(Sesion s: sesiones){
            lista.add(new SesionDTO(s));
        }
        return lista;
    }
}