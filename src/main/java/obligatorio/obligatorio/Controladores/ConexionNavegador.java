package obligatorio.obligatorio.Controladores;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Maneja la conexión SSE (Server-Sent Events) para cada sesión de propietario.
 * Permite enviar notificaciones en tiempo real al navegador del propietario.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConexionNavegador {

    private SseEmitter conexionSSE;
    private ScheduledExecutorService heartbeatExecutor;
    private ScheduledFuture<?> heartbeatTask;

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
        conexionSSE.onCompletion(() -> {
            System.out.println("SSE conexión completada");
            detenerHeartbeat();
        });
        conexionSSE.onTimeout(() -> {
            System.out.println("SSE conexión timeout");
            cerrarConexion();
        });
        conexionSSE.onError((ex) -> {
            System.out.println("SSE error: " + ex.getMessage());
            cerrarConexion();
        });
        
        // Enviar comentario inicial inmediatamente (CRÍTICO para Chrome)
        try {
            // Chrome necesita recibir datos inmediatamente o puede cerrar la conexión
            conexionSSE.send(SseEmitter.event()
                .comment("SSE connection established"));
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo enviar comentario inicial: " + e.getMessage());
        }
        
        // Enviar mensaje inicial de confirmación
        try {
            conexionSSE.send(SseEmitter.event()
                .name("connected")
                .data("{\"tipo\":\"CONEXION_ESTABLECIDA\",\"mensaje\":\"Conexión SSE establecida correctamente\"}"));
            System.out.println("✅ Mensaje inicial SSE enviado");
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo enviar mensaje inicial: " + e.getMessage());
        }
        
        // Iniciar heartbeat para mantener la conexión viva (CRÍTICO para Chrome)
        iniciarHeartbeat();
    }
    
    /**
     * Inicia el envío periódico de comentarios para mantener la conexión SSE viva.
     * Chrome tiende a cerrar conexiones inactivas más rápido que Firefox.
     */
    private void iniciarHeartbeat() {
        detenerHeartbeat(); // Asegurar que no haya uno previo
        
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatTask = heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                if (conexionSSE != null) {
                    // Enviar comentario vacío cada 15 segundos para mantener conexión viva
                    conexionSSE.send(SseEmitter.event().comment("heartbeat"));
                }
            } catch (Exception e) {
                System.out.println("Error en heartbeat SSE: " + e.getMessage());
                detenerHeartbeat();
            }
        }, 15, 15, TimeUnit.SECONDS);
    }
    
    /**
     * Detiene el heartbeat.
     */
    private void detenerHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
            heartbeatTask = null;
        }
        if (heartbeatExecutor != null) {
            heartbeatExecutor.shutdown();
            heartbeatExecutor = null;
        }
    }

    /**
     * Cierra la conexión SSE actual.
     */
    public void cerrarConexion() {
        try {
            detenerHeartbeat();
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
            System.out.println("⚠️ No se puede enviar mensaje: conexión SSE es null");
            return;
        }
        
        try {
            // CRÍTICO: Usar .data() para formato correcto de SSE (compatible con Chrome)
            conexionSSE.send(SseEmitter.event()
                .data(mensaje)
                .build());
            System.out.println("📤 Mensaje SSE enviado: " + mensaje.substring(0, Math.min(50, mensaje.length())));
        } catch (Throwable e) {
            System.out.println("❌ Error al enviar mensaje SSE: " + e.getMessage());
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
