package obligatorio.obligatorio.Controladores;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.Administrador;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.Modelo.modelos.Sesion;

@RestController
@RequestMapping("/acceso")
public class ControladorLogin {

    @PostMapping("/loginPropietario")
    public List<Respuesta> loginPropietario(
            @RequestParam String cedula,
            @RequestParam String password,
            HttpSession sesionHttp) throws ObligatorioException {
        Sesion sesion = Fachada.getInstancia().loginPropietario(cedula, password);
        sesionHttp.setAttribute("usuarioPropietario", sesion);
        sesionHttp.setAttribute("propietario", sesion.getPropietario());

        return Respuesta.lista(new Respuesta("loginExitoso", "TableroControlPropietario.html"));
    }

    @PostMapping("/loginAdministrador")
    public List<Respuesta> loginAdministrador(
            @RequestParam String cedula,
            @RequestParam String password,
            HttpSession sesionHttp) throws ObligatorioException {
        Administrador admin = Fachada.getInstancia().loginAdministrador(cedula, password);
        sesionHttp.setAttribute("usuarioAdmin", admin);
        return Respuesta.lista(new Respuesta("loginExitoso", "panel-admin.html"));
    }

    @PostMapping("/logoutPropietario")
    public List<Respuesta> logoutPropietario(
            @SessionAttribute(name = "usuarioPropietario", required = false) Sesion sesion,
            HttpSession sesionHttp) throws ObligatorioException {
        if (sesion != null) {
            try {
                Fachada.getInstancia().logout(sesion);
            } catch (Exception e) {
                // Sesión ya removida, continuar igual
                System.out.println("⚠️ Sesión ya removida del sistema: " + e.getMessage());
            }
            sesionHttp.removeAttribute("usuarioPropietario");
            sesionHttp.removeAttribute("propietario");
        }
        return Respuesta.lista(new Respuesta("paginaLogin", "login.html"));
    }

    @PostMapping("/logoutAdmin")
    public List<Respuesta> logoutAdmin(
            @SessionAttribute(name = "usuarioAdmin", required = false) Administrador admin,
            HttpSession sesionHttp) {
        if (admin != null) {
            Fachada.getInstancia().logoutAdministrador(admin.getCedula());
            sesionHttp.removeAttribute("usuarioAdmin");
        }
        return Respuesta.lista(new Respuesta("paginaLogin", "login-admin.html"));
    }

}
