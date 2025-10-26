package obligatorio.obligatorio.Controladores;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.Modelo.Fachada;
import obligatorio.obligatorio.Modelo.ObligatorioException;
import obligatorio.obligatorio.Modelo.Propietario;
import obligatorio.obligatorio.Modelo.Sesion;

@RestController
@RequestMapping("/propietario")
public class CasoUsoTableroPropietario {

    private Propietario propietarioEnSesion(HttpSession sesionHttp) throws ObligatorioException {
        Object obj = sesionHttp.getAttribute("usuarioPropietario");
        if (obj instanceof Sesion s && s.getPropietario() != null) {
            return s.getPropietario();
        }
        throw new ObligatorioException("Sesión expirada o no iniciada");
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
