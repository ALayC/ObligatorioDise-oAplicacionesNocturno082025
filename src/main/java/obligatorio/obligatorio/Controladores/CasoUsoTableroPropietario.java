package obligatorio.obligatorio.Controladores;

import java.util.List;

import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import obligatorio.obligatorio.Modelo.Fachada;
import obligatorio.obligatorio.Modelo.ObligatorioException;
import obligatorio.obligatorio.Modelo.Propietario;

@RestController
@RequestMapping("/propietario")
public class CasoUsoTableroPropietario {

    @PostMapping("/tablero")
    public Object cargarTablero(@SessionAttribute(name = "propietario", required = false) Propietario p) {
        if (p == null) {
            // 400 => la vista ejecuta procesarErrorSubmit y va a login.html
            return ResponseEntity.badRequest().body("Sesión expirada o no iniciada");
        }
        return Fachada.getInstancia().armarRespuestasTablero(p);
    }

    @PostMapping("/notificaciones/borrar")
    public Object borrarNotificaciones(@SessionAttribute(name = "propietario", required = false) Propietario p)
            throws ObligatorioException {
        if (p == null) {
            return ResponseEntity.badRequest().body("Sesión expirada o no iniciada");
        }
        int cant = Fachada.getInstancia().borrarNotificaciones(p);
        if (cant == 0) {
            // mensaje de negocio
            HttpHeaders h = new HttpHeaders();
            h.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
            return new ResponseEntity<>("No hay notificaciones para borrar", h, HttpStatusCode.valueOf(299));
        }
        return Respuesta.lista(
                new Respuesta("notificacionesBorradas", cant),
                new Respuesta("notificaciones", List.of()));
    }

    @PostMapping("/recargarSaldo")
    public Object recargarSaldo(
            @SessionAttribute(name = "propietario", required = false) Propietario p,
            @RequestParam BigDecimal monto) throws ObligatorioException {

        if (p == null) {
            return ResponseEntity.badRequest().body("Sesión expirada o no iniciada");
        }

        Fachada.getInstancia().recargarSaldo(p, monto);

        return Fachada.getInstancia().armarRespuestasTablero(p);
    }
}