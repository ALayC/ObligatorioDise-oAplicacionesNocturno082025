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

import obligatorio.obligatorio.DTO.PropietarioBusquedaDTO;
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
    private final Fachada fachada = Fachada.getInstancia();

    public ControladorCambioEstadoPropietario(@Autowired ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter registrarSSE(
            @SessionAttribute(name = "usuarioAdmin", required = false) Object usuarioAdmin) {
        System.out.println("[SSE] registrarSSE llamado por usuarioAdmin: " + usuarioAdmin);
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    @PostMapping("/vistaConectada")
    public Object vistaConectada() {
        System.out.println("[POST] vistaConectada llamado");
        List<String> estados = fachada.getEstadosPropietarioDefinidos();
        // Enviamos los estados para llenar el combo apenas carga la vista
        return Respuesta.lista(
                new Respuesta("estados", estados));
    }

    @PostMapping("/vistaCerrada")
    public void vistaCerrada() {
        System.out.println("[POST] vistaCerrada llamado");
        if (propietario != null) {
            propietario.quitarObservador(this);
            propietario = null;
        }
    }

    @PostMapping("/buscar")
    public Object buscarPropietario(@RequestParam String cedula) {
        System.out.println("[POST] buscarPropietario llamado con cedula: " + cedula);

        if (this.propietario != null) {
            this.propietario.quitarObservador(this);
        }

        propietario = fachada.getPropietarioPorCedula(cedula);
        if (propietario == null) {
            return Respuesta.lista(
                    new Respuesta("propietarioBusqueda", null),
                    new Respuesta("mensajeEstado", "No existe el propietario"));
        }

        propietario.agregarObservador(this);

        PropietarioBusquedaDTO dto = new PropietarioBusquedaDTO(
                propietario.getCedula(),
                propietario.getNombreCompleto(),
                propietario.getEstadoPropietario() != null ? propietario.getEstadoPropietario().getNombre() : "");

        return Respuesta.lista(
                new Respuesta("propietarioBusqueda", dto),
                new Respuesta("mensajeEstado", ""));
    }

    @PostMapping("/cambiar")
    public Object cambiarEstado(@RequestParam String cedula, @RequestParam String nuevoEstado) {
        System.out.println("[POST] cambiarEstado llamado con cedula: " + cedula + ", nuevoEstado: " + nuevoEstado);

        Propietario p = fachada.getPropietarioPorCedula(cedula);
        if (p == null) {
            return Respuesta.lista(
                    new Respuesta("mensajeEstado", "No existe el propietario"),
                    new Respuesta("propietarioBusqueda", null));
        }

        // Me aseguro de estar observando al propietario correcto
        if (this.propietario != null && !this.propietario.equals(p)) {
            this.propietario.quitarObservador(this);
        }
        this.propietario = p;
        this.propietario.agregarObservador(this);

        String resultado = fachada.cambiarEstadoPropietarioYNotificar(cedula, nuevoEstado);

        // Releer el estado actualizado
        p = fachada.getPropietarioPorCedula(cedula);

        PropietarioBusquedaDTO dto = null;
        if (p != null) {
            dto = new PropietarioBusquedaDTO(
                    p.getCedula(),
                    p.getNombreCompleto(),
                    p.getEstadoPropietario() != null ? p.getEstadoPropietario().getNombre() : "");
        }

        return Respuesta.lista(
                new Respuesta("propietarioBusqueda", dto),
                new Respuesta("mensajeEstado", resultado));
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        if (!(origen instanceof Propietario)) {
            return;
        }
        Propietario p = (Propietario) origen;

        if (this.propietario == null || !this.propietario.equals(p)) {
            return;
        }
        if (!Propietario.Eventos.CAMBIO_ESTADO.equals(evento)) {
            return;
        }
        if (conexionNavegador.getConexionSSE() == null) {
            return;
        }

        PropietarioBusquedaDTO dto = new PropietarioBusquedaDTO(
                p.getCedula(),
                p.getNombreCompleto(),
                p.getEstadoPropietario() != null ? p.getEstadoPropietario().getNombre() : "");

        // SSE: mismo formato que otras vistas → Respuesta.lista(...)
        conexionNavegador.enviarJSON(
                Respuesta.lista(
                        new Respuesta("cambioEstadoPropietario", dto)));
    }
}
