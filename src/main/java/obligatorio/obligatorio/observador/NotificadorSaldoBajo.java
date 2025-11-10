package obligatorio.obligatorio.observador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import obligatorio.obligatorio.Controladores.ConexionNavegador;
import obligatorio.obligatorio.Modelo.modelos.Notificacion;
import obligatorio.obligatorio.Modelo.modelos.Propietario;

public class NotificadorSaldoBajo implements Observador {

    private ConexionNavegador conexionNavegador; // Opcional, para SSE
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NotificadorSaldoBajo() {
        // Constructor sin parámetros para uso normal
    }

    public NotificadorSaldoBajo(ConexionNavegador conexionNavegador) {
        // Constructor con conexión SSE
        this.conexionNavegador = conexionNavegador;
    }

    public void setConexionNavegador(ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        if (evento instanceof SaldoBajoEvento saldoEvento) {
            if (origen instanceof Propietario propietario) {
                // Verificar si el saldo está por debajo del mínimo de alerta
                if (propietario.getSaldoActual().compareTo(propietario.getSaldoMinimoAlerta()) < 0) {
                    String fechaActual = LocalDateTime.now().format(FORMATO_FECHA);
                    String mensaje = fechaActual + 
                                   " Tu saldo actual es de $ " + saldoEvento.getSaldoActual() + 
                                   " Te recomendamos hacer una recarga";
                    
                    propietario.agregarNotificacion(new Notificacion(mensaje, LocalDateTime.now()));
                    
                    // Si hay conexión SSE, enviar notificación en tiempo real
                    if (conexionNavegador != null) {
                        Map<String, Object> notif = new HashMap<>();
                        notif.put("tipo", "SALDO_BAJO");
                        notif.put("mensaje", mensaje);
                        conexionNavegador.enviarJSON(notif);
                    }
                }
            }
        }
    }
}
