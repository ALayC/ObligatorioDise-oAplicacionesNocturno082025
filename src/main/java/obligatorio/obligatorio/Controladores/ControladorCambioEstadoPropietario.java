
package obligatorio.obligatorio.Controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.ConexionNavegador;
import obligatorio.obligatorio.Modelo.modelos.Propietario;
import obligatorio.obligatorio.observador.Observable;
import obligatorio.obligatorio.observador.Observador;

@RestController
@RequestMapping("/cambiar-estado-propietario")
@Scope("session")
public class ControladorCambioEstadoPropietario implements Observador {
    private Propietario propietario;
    private final ConexionNavegador conexionNavegador;

    public ControladorCambioEstadoPropietario(@Autowired ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter registrarSSE(@SessionAttribute(name = "usuarioAdmin", required = false) Object usuarioAdmin) {
        System.out.println("[SSE] registrarSSE llamado por usuarioAdmin: " + usuarioAdmin);
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    @PostMapping("/vistaConectada")
    public void inicializarVista(@RequestParam String cedula) {
        System.out.println("[POST] inicializarVista llamado con cedula: " + cedula);
        if (propietario != null) {
            propietario.agregarObservador(this);
        }
    }

    @PostMapping("/vistaCerrada")
    public void salir() {
        System.out.println("[POST] vistaCerrada llamado");
        if (propietario != null) {
            propietario.quitarObservador(this);
        }
    }

    @GetMapping("/buscar")
    public obligatorio.obligatorio.DTO.PropietarioBusquedaDTO buscarPropietario(@RequestParam String cedula) {
        System.out.println("[GET] buscarPropietario llamado con cedula: " + cedula);
        propietario = Fachada.getInstancia().getPropietarioPorCedula(cedula);
        if (propietario == null) return null;
        return new obligatorio.obligatorio.DTO.PropietarioBusquedaDTO(
            propietario.getCedula(),
            propietario.getNombreCompleto(),
            propietario.getEstadoPropietario() != null ? propietario.getEstadoPropietario().getNombre() : ""
        );
    }
    

    @GetMapping("/estados")
    public List<String> obtenerEstados() {
        System.out.println("[GET] obtenerEstados llamado");
        // Lista fija de estados posibles (puedes mover esto a Fachada si lo prefieres)
        return List.of("Habilitado", "Deshabilitado", "Suspendido", "Penalizado");
    }

    @PostMapping("/cambiar")
    public String cambiarEstado(@RequestParam String cedula, @RequestParam String nuevoEstado) {
        System.out.println("[POST] cambiarEstado llamado con cedula: " + cedula + ", nuevoEstado: " + nuevoEstado);
        String resultado = Fachada.getInstancia().cambiarEstadoPropietarioYNotificar(cedula, nuevoEstado);
        propietario = Fachada.getInstancia().getPropietarioPorCedula(cedula);
        if (conexionNavegador.getConexionSSE() != null) {
            conexionNavegador.enviarJSON(propietario);
        }

        return resultado;
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        // Enviar SSE cuando el estado del propietario cambie
        if (conexionNavegador.getConexionSSE() != null) {
            conexionNavegador.enviarJSON(propietario);
        }
    }
}
