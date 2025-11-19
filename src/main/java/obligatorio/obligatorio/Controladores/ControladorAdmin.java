
package obligatorio.obligatorio.Controladores;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.DTO.ResultadoEmulacionDTO;
import obligatorio.obligatorio.DTO.TarifaDTO;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.Administrador;
import obligatorio.obligatorio.Modelo.modelos.ConexionNavegador;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.observador.Observable;
import obligatorio.obligatorio.observador.Observador;

/**
 * Controlador para CU Emular tránsito + Monitor en tiempo real (SSE).
 * Actúa como Observador de la Fachada, con scope de sesión (un observador por
 * admin conectado).
 */
@RestController
@RequestMapping("/admin")
@Scope("session")
public class ControladorAdmin implements Observador {

    private final ConexionNavegador conexionNavegador;

    public ControladorAdmin(@Autowired ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE(@SessionAttribute(name = "usuarioAdmin") Administrador admin) {
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    /**
     * Endpoint inicial que registra al controlador como observador y devuelve datos
     * base.
     */
    @PostMapping("/vistaConectada")
    public Object vistaConectada(@SessionAttribute(name = "usuarioAdmin") Administrador admin) {
        // Obtener lista de puestos como DTOs desde el sistema
        List<PuestoDTO> puestosDTO = Fachada.getInstancia().getPuestosDTO();

        // Registrar como observador (única vez por sesión)
        Fachada.getInstancia().agregarObservador(this);

        return Respuesta.lista(
                new Respuesta("infoAdmin", admin.getNombreCompleto()),
                new Respuesta("puestos", puestosDTO));
    }

    @PostMapping("/obtenerTarifas")
    public Object obtenerTarifas(
            @RequestParam("nombrePuesto") String nombrePuesto,
            @SessionAttribute(name = "usuarioAdmin") Administrador admin) {
        try {
            // Obtener tarifas como DTOs desde el sistema
            List<TarifaDTO> tarifasDTO = Fachada.getInstancia().getTarifasPorPuesto(nombrePuesto);

            return Respuesta.lista(
                    new Respuesta("tarifas", tarifasDTO));

        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/emularTransito")
    public Object emularTransito(
            @RequestParam("matricula") String matricula,
            @RequestParam("nombrePuesto") String nombrePuesto,
            @RequestParam("fecha") String fechaStr,
            @RequestParam("hora") String horaStr,
            @SessionAttribute(name = "usuarioAdmin") Administrador admin) {
        try {
            // Parsear fecha y hora
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
            java.time.LocalTime hora = java.time.LocalTime.parse(horaStr, DateTimeFormatter.ISO_LOCAL_TIME);

            // Emular tránsito
            ResultadoEmulacionDTO resultado = Fachada.getInstancia()
                    .emularTransito(matricula, nombrePuesto, fecha, hora);

            return Respuesta.lista(
                    new Respuesta("resultadoEmulacion", resultado));

        } catch (ObligatorioException e) {
            return ResponseEntity.status(299).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    @PostMapping("/vistaCerrada")
    public void vistaCerrada() {
        Fachada.getInstancia().quitarObservador(this);
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        if (!(evento instanceof Fachada.Eventos))
            return;
        Fachada.Eventos ev = (Fachada.Eventos) evento;
        switch (ev) {
            case transitoRegistrado -> {
                if (conexionNavegador.getConexionSSE() != null) {
                    conexionNavegador.enviarJSON(Fachada.getInstancia().getTransitos());
                }
            }
            case saldoActualizado -> {
                if (conexionNavegador.getConexionSSE() != null) {
                    conexionNavegador.enviarJSON(Fachada.getInstancia().getSesiones());
                }
            }
            case notificacionesActualizadas -> {

                if (conexionNavegador.getConexionSSE() != null) {
                    // Aquí podrías enviar notificaciones si tienes un método para obtenerlas
                    // conexionNavegador.enviarJSON(Fachada.getInstancia().getNotificacionesGlobales());
                }
            }
        }
    }

}