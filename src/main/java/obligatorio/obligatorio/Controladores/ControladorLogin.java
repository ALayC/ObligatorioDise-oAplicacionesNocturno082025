package obligatorio.obligatorio.Controladores;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.Modelo.Administrador;
import obligatorio.obligatorio.Modelo.Fachada;
import obligatorio.obligatorio.Modelo.ObligatorioException;
import obligatorio.obligatorio.Modelo.Sesion;

@RestController
@RequestMapping("/acceso")
public class ControladorLogin {

@PostMapping("/loginPropietario")
public List<Respuesta> loginPropietario(HttpSession sesionHttp,
                                        @RequestParam String cedula,
                                        @RequestParam String password) throws ObligatorioException {
    Sesion sesion = Fachada.getInstancia().loginPropietario(cedula, password);
    logoutPropietario(sesionHttp);
    sesionHttp.setAttribute("usuarioPropietario", sesion);
    sesionHttp.setAttribute("propietario", sesion.getPropietario());

    return Respuesta.lista(new Respuesta("loginExitoso", "TableroControlPropietario.html"));
}

    @PostMapping("/loginAdministrador")
    public List<Respuesta> loginAdministrador(HttpSession sesionHttp,
                                              @RequestParam String cedula,
                                              @RequestParam String password) throws ObligatorioException {
        Administrador admin = Fachada.getInstancia().loginAdministrador(cedula, password);
        sesionHttp.setAttribute("usuarioAdmin", admin);
        return Respuesta.lista(new Respuesta("loginExitoso", "monitor-actividad.html"));
    }

    @PostMapping("/logoutPropietario")
    public List<Respuesta> logoutPropietario(HttpSession sesionHttp) throws ObligatorioException {
        Object obj = sesionHttp.getAttribute("usuarioPropietario");
        if (obj instanceof Sesion s) {
            Fachada.getInstancia().logout(s);
            sesionHttp.removeAttribute("usuarioPropietario");
        }
        return Respuesta.lista(new Respuesta("paginaLogin", "login.html"));
    }

    @PostMapping("/logoutAdmin")
    public List<Respuesta> logoutAdmin(HttpSession sesionHttp) {
        Object obj = sesionHttp.getAttribute("usuarioAdmin");
        if (obj instanceof Administrador a) {
            obligatorio.obligatorio.Modelo.Fachada.getInstancia().logoutAdministrador(a.getCedula());
            sesionHttp.removeAttribute("usuarioAdmin");
        }
        return Respuesta.lista(new Respuesta("paginaLogin", "login-admin.html"));
    }

}
