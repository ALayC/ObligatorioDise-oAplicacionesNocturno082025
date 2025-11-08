package obligatorio.obligatorio.Controladores;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Maneja la conexión SSE (Server-Sent Events) para cada sesión de propietario.
 * Permite enviar notificaciones en tiempo real al navegador del propietario.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConexionNavegador {

    private SseEmitter conexionSSE;

    /**
     * Establece una nueva conexión SSE.
     * Si ya existe una conexión, la cierra antes de crear una nueva.
     */
    public void conectarSSE() {
        if (conexionSSE != null) {
            cerrarConexion();
        }
        // 30 minutos de timeout (igual al valor por defecto de la sesión)
        long timeOut = 30 * 60 * 1000;
        conexionSSE = new SseEmitter(timeOut);
        
        // Configurar callback para cuando se cierre la conexión
        conexionSSE.onCompletion(() -> System.out.println("SSE conexión completada"));
        conexionSSE.onTimeout(() -> {
            System.out.println("SSE conexión timeout");
            cerrarConexion();
        });
        conexionSSE.onError((ex) -> {
            System.out.println("SSE error: " + ex.getMessage());
            cerrarConexion();
        });
    }

    /**
     * Cierra la conexión SSE actual.
     */
    public void cerrarConexion() {
        try {
            if (conexionSSE != null) {
                conexionSSE.complete();
                conexionSSE = null;
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar conexión SSE: " + e.getMessage());
        }
    }

    /**
     * Obtiene la conexión SSE actual.
     */
    public SseEmitter getConexionSSE() {
        return conexionSSE;
    }

    /**
     * Envía un objeto como JSON a través de SSE.
     * 
     * @param informacion Objeto a enviar (será convertido a JSON)
     */
    public void enviarJSON(Object informacion) {
        try {
            String json = new ObjectMapper().writeValueAsString(informacion);
            enviarMensaje(json);
        } catch (JsonProcessingException e) {
            System.out.println("Error al convertir a JSON: " + e.getMessage());
        }
    }

    /**
     * Envía un mensaje de texto a través de SSE.
     * 
     * @param mensaje Mensaje a enviar
     */
    public void enviarMensaje(String mensaje) {
        if (conexionSSE == null) {
            return;
        }
        
        try {
            conexionSSE.send(mensaje);
            System.out.println("📤 Mensaje SSE enviado: " + mensaje.substring(0, Math.min(50, mensaje.length())));
        } catch (Throwable e) {
            System.out.println("Error al enviar mensaje SSE: " + e.getMessage());
            cerrarConexion();
        }
    }

    /**
     * Verifica si hay una conexión SSE activa.
     */
    public boolean estaConectado() {
        return conexionSSE != null;
    }
}
