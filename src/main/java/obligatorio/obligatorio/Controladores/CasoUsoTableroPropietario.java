package obligatorio.obligatorio.Controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.ConexionNavegador;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.Modelo.modelos.Sesion;
import obligatorio.obligatorio.observador.Observable;
import obligatorio.obligatorio.observador.Observador;

@RestController
@RequestMapping("/propietario")
@Scope("session")
public class CasoUsoTableroPropietario implements Observador {
    private Propietario propietario;
    private final ConexionNavegador conexionNavegador;
    public CasoUsoTableroPropietario(@Autowired ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }
    
    @PostMapping("/vistaConectada")
    public List<Respuesta> inicializarVista(@SessionAttribute(name = "usuarioPropietario") Sesion sesion){
        propietario = sesion.getPropietario();
        propietario.agregarObservador(this);
        return Fachada.getInstancia().armarRespuestasTablero(propietario);
    }

    @PostMapping("/vistaCerrada")
    public void salir(){
        if(propietario != null) propietario.quitarObservador(this);
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        if (evento instanceof Propietario.Eventos ev) {
            switch (ev) {
                case TRANSITO_REALIZADO, SALDO_BAJO -> 
                    conexionNavegador.enviarJSON(Fachada.getInstancia().armarRespuestasTablero(propietario));
            }
        }
    }



    @PostMapping("/notificaciones/borrar")
    public Object borrarNotificaciones(@SessionAttribute(name = "usuarioPropietario") Sesion sesion) {
        Propietario p = sesion.getPropietario();
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
    }

}
