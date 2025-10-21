package obligatorio.obligatorio.Controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.Modelo.Fachada;
import obligatorio.obligatorio.Modelo.ObligatorioException;
import obligatorio.obligatorio.Modelo.Propietario;

@RestController
@RequestMapping("/acceso")

public class ControladorLogin {

    @PostMapping("/loginPropietario")
    public List<Respuesta> loginPropietario(HttpSession sesionHttp, @RequestParam String username,
            @RequestParam String password) throws ObligatorioException {

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ObligatorioException("Usuario y contraseña son obligatorios");
        }

        // login al modelo
        Propietario propietario = Fachada.getInstancia().login(username, password);
        // guardo el usuario en la sesion
        sesionHttp.setAttribute("propietario", propietario);
        return Respuesta.lista(new Respuesta("loginExitoso", "TableroControlPropietario.html"));
    }

    @PostMapping("/acceso/logout")
    public ResponseEntity<String> logout(HttpSession sesion) {
        if (sesion != null)
            sesion.invalidate();
        return ResponseEntity.ok("OK");
    }
}
