package obligatorio.obligatorio.observador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import obligatorio.obligatorio.Controladores.ConexionNavegador;
import obligatorio.obligatorio.Modelo.modelos.Notificacion;
import obligatorio.obligatorio.Modelo.modelos.Propietario;

public class NotificadorTransito implements Observador {

    private ConexionNavegador conexionNavegador; // Opcional, para SSE
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NotificadorTransito() {
        // Constructor sin parámetros para uso normal
    }

    public NotificadorTransito(ConexionNavegador conexionNavegador) {
        // Constructor con conexión SSE
        this.conexionNavegador = conexionNavegador;
    }

    public void setConexionNavegador(ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @Override
    public void actualizar(Observable origen, Object evento) {
        if (evento instanceof TransitoRealizadoEvento transitoEvento) {
            if (origen instanceof Propietario propietario) {
                // Crear notificación de tránsito realizado
                String fechaTransito = transitoEvento.getFechaHora().format(FORMATO_FECHA);
                String mensaje = fechaTransito + 
                               " Pasaste por el puesto " + transitoEvento.getPuesto().getNombre() + 
                               " con el vehículo " + transitoEvento.getVehiculo().getMatricula();
                
                propietario.agregarNotificacion(new Notificacion(mensaje, LocalDateTime.now()));
                
                // Si hay conexión SSE, enviar notificación en tiempo real
                if (conexionNavegador != null && conexionNavegador.estaConectado()) {
                    Map<String, Object> notif = new HashMap<>();
                    notif.put("tipo", "TRANSITO_REALIZADO");
                    notif.put("mensaje", mensaje);
                    conexionNavegador.enviarJSON(notif);
                }
            }
        }
    }
}
