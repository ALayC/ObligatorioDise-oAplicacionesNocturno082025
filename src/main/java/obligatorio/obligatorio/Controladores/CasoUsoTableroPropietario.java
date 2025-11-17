package obligatorio.obligatorio.Controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    @GetMapping(value = "/registrarSSE", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter registrarSSE(@SessionAttribute(name = "usuarioPropietario") Sesion sesion) {
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }
    @PostMapping("/vistaConectada")
    public List<Respuesta> inicializarVista(@SessionAttribute(name = "usuarioPropietario") Sesion sesion){
        propietario = sesion.getPropietario();
        System.out.println("[LOG] Registrando observador en propietario: " + propietario.getNombreCompleto());
        propietario.agregarObservador(this);
        return Fachada.getInstancia().armarRespuestasTablero(propietario);
    }

    @PostMapping("/vistaCerrada")
    public void salir(){
        if(propietario != null) {
            System.out.println("[LOG] Quitando observador de propietario: " + propietario.getNombreCompleto());
            propietario.quitarObservador(this);
        }
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        System.out.println("[LOG] CasoUsoTableroPropietario.actualizar llamado. Evento: " + evento);
        if (evento instanceof Propietario.Eventos ev) {
            switch (ev) {
                case TRANSITO_REALIZADO, SALDO_BAJO, CAMBIO_ESTADO -> {
                    System.out.println("[LOG] Enviando SSE al propietario: " + propietario.getNombreCompleto());
                    conexionNavegador.enviarJSON(Fachada.getInstancia().armarRespuestasTablero(propietario));
                }
            }
        }
    }

    @PostMapping("/notificaciones/borrar")
    public Object borrarNotificaciones(@SessionAttribute(name = "usuarioPropietario") Sesion sesion) {
        Propietario p = sesion.getPropietario();
        int cant = Fachada.getInstancia().borrarNotificaciones(p);
        if (cant == 0) {
            return ResponseEntity.status(299).body("No hay notificaciones para borrar");
        }
        return Respuesta.lista(
            new Respuesta("notificacionesBorradas", cant),
            new Respuesta("notificaciones", List.of())
        );
    }

}
