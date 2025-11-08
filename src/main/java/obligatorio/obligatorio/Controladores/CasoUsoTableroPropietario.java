package obligatorio.obligatorio.Controladores;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import obligatorio.obligatorio.observador.NotificadorSaldoBajo;
import obligatorio.obligatorio.observador.NotificadorTransito;
import obligatorio.obligatorio.observador.Observador;

@RestController
@RequestMapping("/propietario")
public class CasoUsoTableroPropietario {

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
    public SseEmitter conectarSSE(HttpSession sesionHttp) {
        try {
            Propietario propietario = propietarioEnSesion(sesionHttp);
            
            // Establecer conexión SSE
            conexionNavegador.conectarSSE();
            
            // Registrar los observadores con la conexión SSE (solo si no están ya registrados)
            // Buscar si ya existen observadores de estos tipos
            boolean tieneNotificadorTransito = false;
            boolean tieneNotificadorSaldoBajo = false;
            
            for (Observador obs : propietario.getObservadores()) {
                if (obs instanceof NotificadorTransito) {
                    tieneNotificadorTransito = true;
                    ((NotificadorTransito) obs).setConexionNavegador(conexionNavegador);
                } else if (obs instanceof NotificadorSaldoBajo) {
                    tieneNotificadorSaldoBajo = true;
                    ((NotificadorSaldoBajo) obs).setConexionNavegador(conexionNavegador);
                }
            }
            
            // Si no existen, crearlos y agregarlos
            if (!tieneNotificadorTransito) {
                propietario.agregarObservador(new NotificadorTransito(conexionNavegador));
            }
            if (!tieneNotificadorSaldoBajo) {
                propietario.agregarObservador(new NotificadorSaldoBajo(conexionNavegador));
            }
            
            System.out.println("✅ Conexión SSE establecida para " + propietario.getNombreCompleto());
            
            return conexionNavegador.getConexionSSE();
            
        } catch (ObligatorioException e) {
            System.out.println("❌ Error al establecer SSE: " + e.getMessage());
            return null;
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
}
