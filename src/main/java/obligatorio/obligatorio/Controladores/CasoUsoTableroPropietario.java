package obligatorio.obligatorio.Controladores;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Sesion;
import obligatorio.obligatorio.observador.Observable;
import obligatorio.obligatorio.observador.Observador;

@RestController
@RequestMapping("/propietario")
@Scope("session")
public class CasoUsoTableroPropietario implements Observador {

    @Autowired
    private ConexionNavegador conexionNavegador;

    private Propietario propietarioEnSesion(HttpSession sesionHttp) throws ObligatorioException {
        Object obj = sesionHttp.getAttribute("usuarioPropietario");
        if (obj instanceof Sesion s && s.getPropietario() != null) {
            return s.getPropietario();
        }
        throw new ObligatorioException("Sesión expirada o no iniciada");
    }

    /**
     * Endpoint para establecer conexión SSE (Server-Sent Events).
     * El cliente JavaScript se conecta aquí para recibir notificaciones en tiempo real.
     */
    @GetMapping(value = "/sse/conectar", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> conectarSSE(HttpSession sesionHttp) {
        try {
            Propietario propietario = propietarioEnSesion(sesionHttp);         
            // Establecer conexión SSE
            conexionNavegador.conectarSSE();        
            // Registrar este controlador como observador del propietario (si no está ya registrado)
            if (!propietario.getObservadores().contains(this)) {
                propietario.agregarObservador(this);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_EVENT_STREAM);
            headers.setCacheControl("no-cache, no-transform");
            headers.set("Connection", "keep-alive"); // CRÍTICO para Chrome
            headers.set("X-Accel-Buffering", "no"); // Disable nginx buffering       
            return ResponseEntity.ok()
                .headers(headers)
                .body(conexionNavegador.getConexionSSE());
            
        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Implementación del patrón Observador: recibe eventos del Propietario.
     * Similar al ejemplo de Agenda, usa switch sobre el enum de eventos.
     */
    @Override
    public void actualizar(Object evento, Observable origen) {
        // Verificar que el evento sea del tipo correcto y el origen sea Propietario
        if (!(evento instanceof Propietario.Eventos)) return;
        if (!(origen instanceof Propietario)) return;
        
        Propietario.Eventos ev = (Propietario.Eventos) evento;
        Propietario propietario = (Propietario) origen;
        
        // Switch sobre el enum (estilo Agenda)
        switch (ev) {
            case TRANSITO_REALIZADO -> enviarNotificacionTransito(propietario);
        }
    }
    
    /**
     * Envía notificación de tránsito realizado vía SSE.
     */
    private void enviarNotificacionTransito(Propietario propietario) {
        var notificaciones = propietario.getNotificaciones();
        if (!notificaciones.isEmpty()) {
            var ultimaNotificacion = notificaciones.get(notificaciones.size() - 1);
            
            // Crear estructura JSON con tipo de notificación
            java.util.Map<String, Object> notificacion = new java.util.HashMap<>();
            notificacion.put("tipo", "TRANSITO_REALIZADO");
            notificacion.put("mensaje", ultimaNotificacion.getMensaje());
            notificacion.put("fechaHora", ultimaNotificacion.getFechaHora().toString());
            
            if (conexionNavegador != null) {
                conexionNavegador.enviarJSON(notificacion);
            }
        }
    }

@PostMapping("/tablero")
public Object cargarTablero(HttpSession sesionHttp) {
    Object obj = sesionHttp.getAttribute("usuarioPropietario");
    if (obj instanceof Sesion s && s.getPropietario() != null) {
        Propietario p = s.getPropietario();
        System.out.printf(
            "DEBUG tablero: cedula=%s | vehiculos=%d | asignaciones=%d | notificaciones=%d | transitos=%d%n",
            p.getCedula(),
            p.getVehiculos().size(),
            p.getAsignaciones().size(),
            p.getNotificaciones().size(),
            Fachada.getInstancia().getTransitos().size()
        );
        return Fachada.getInstancia().armarRespuestasTablero(p);
    }
    return ResponseEntity.badRequest().body("Sesión expirada o no iniciada");
}

    @PostMapping("/notificaciones/borrar")
    public Object borrarNotificaciones(HttpSession sesionHttp) {
        try {
            Propietario p = propietarioEnSesion(sesionHttp);
            int cant = Fachada.getInstancia().borrarNotificaciones(p);
            if (cant == 0) {
                HttpHeaders h = new HttpHeaders();
                h.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
                return new ResponseEntity<>("No hay notificaciones para borrar", h, HttpStatusCode.valueOf(299));
            }
            return Respuesta.lista(
                new Respuesta("notificacionesBorradas", cant),
                new Respuesta("notificaciones", List.of())
            );
        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/recargarSaldo")
    public Object recargarSaldo(HttpSession sesionHttp, @RequestParam BigDecimal monto) {
        try {
            Propietario p = propietarioEnSesion(sesionHttp);
            Fachada.getInstancia().recargarSaldo(p, monto);
            return Fachada.getInstancia().armarRespuestasTablero(p);
        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Método para limpiar el observador cuando se cierra la conexión.
     * Debe ser llamado cuando el usuario hace logout o cierra la vista.
     */
    @PostMapping("/desconectar")
    public ResponseEntity<String> desconectar(HttpSession sesionHttp) {
        try {
            Propietario propietario = propietarioEnSesion(sesionHttp);
            propietario.quitarObservador(this);
            conexionNavegador.cerrarConexion();
            System.out.println("👋 Observador desregistrado y conexión SSE cerrada");
            return ResponseEntity.ok("Desconectado");
        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
