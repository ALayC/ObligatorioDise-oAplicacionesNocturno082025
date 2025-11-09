package obligatorio.obligatorio.Controladores;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.DTO.ResultadoEmulacionDTO;
import obligatorio.obligatorio.DTO.TarifaDTO;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.Administrador;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;
import obligatorio.obligatorio.observador.Observable;
import obligatorio.obligatorio.observador.Observador;

/**
 * Controlador para CU Emular tránsito + Monitor en tiempo real (SSE).
 * Actúa como Observador de la Fachada, con scope de sesión (un observador por admin conectado).
 */
@RestController
@RequestMapping("/admin")
@Scope("session")
public class ControladorAdmin implements Observador {

    private final ConexionNavegador conexionNavegador;

    @Autowired
    public ControladorAdmin(ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    // Helper para validar sesión
    private Administrador administradorEnSesion(HttpSession sesionHttp) throws ObligatorioException {
        Object obj = sesionHttp.getAttribute("usuarioAdmin");
        if (obj instanceof Administrador a) {
            return a;
        }
        throw new ObligatorioException("Sesión expirada");
    }

    /**
     * Endpoint inicial que registra al controlador como observador y devuelve datos base.
     */
    @PostMapping("/vistaConectada")
    public Object vistaConectada(HttpSession sesionHttp) {
        try {
            Administrador admin = administradorEnSesion(sesionHttp);
            System.out.println("🔐 Admin " + admin.getNombreCompleto() + " conectó vista");
            
            // Obtener lista de puestos como DTOs desde el sistema
            List<PuestoDTO> puestosDTO = Fachada.getInstancia().getPuestosDTO();

            // Registrar como observador (única vez por sesión)
            Fachada.getInstancia().agregarObservador(this);
            System.out.println("✅ ControladorAdmin registrado como observador. Total observadores: " + Fachada.getInstancia().getObservadores().size());

            return Respuesta.lista(
                new Respuesta("infoAdmin", admin.getNombreCompleto()),
                new Respuesta("puestos", puestosDTO)
            );
            
        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/obtenerTarifas")
    public Object obtenerTarifas(
            @RequestParam("nombrePuesto") String nombrePuesto,
            HttpSession sesionHttp) {
        try {
            administradorEnSesion(sesionHttp);
            
            // Obtener tarifas como DTOs desde el sistema
            List<TarifaDTO> tarifasDTO = Fachada.getInstancia().getTarifasPorPuesto(nombrePuesto);

            return Respuesta.lista(
                new Respuesta("tarifas", tarifasDTO)
            );
            
        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/emularTransito")
    public Object emularTransito(
            @RequestParam("matricula") String matricula,
            @RequestParam("nombrePuesto") String nombrePuesto,
            @RequestParam("fechaHora") String fechaHoraStr,
            HttpSession sesionHttp) {
        try {
            administradorEnSesion(sesionHttp);
            
            // Parsear fecha y hora
            LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr, 
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            // Emular tránsito
            ResultadoEmulacionDTO resultado = Fachada.getInstancia()
                .emularTransito(matricula, nombrePuesto, fechaHora);

            return Respuesta.lista(
                new Respuesta("resultadoEmulacion", resultado)
            );
            
        } catch (ObligatorioException e) {
            return ResponseEntity.status(299).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    /** Registrar conexión SSE del navegador. */
    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE(HttpSession sesionHttp) {
        try {
            Administrador admin = administradorEnSesion(sesionHttp);
            System.out.println("📡 Registrando SSE para admin: " + admin.getNombreCompleto() + " | Session ID: " + sesionHttp.getId());
            conexionNavegador.conectarSSE();
            System.out.println("✅ SSE conectado. Estado: " + conexionNavegador.estaConectado());
            return conexionNavegador.getConexionSSE();
        } catch (ObligatorioException e) {
            System.out.println("❌ Error al registrar SSE: " + e.getMessage());
            return null;
        }
    }

    /** Vista cerrada: quitarse como observador para evitar memory leaks. */
    @PostMapping("/vistaCerrada")
    public void vistaCerrada(HttpSession sesionHttp) {
        System.out.println("👋 Vista cerrada. Quitando observador. Session ID: " + sesionHttp.getId());
        Fachada.getInstancia().quitarObservador(this);
        conexionNavegador.cerrarConexion();
        System.out.println("✅ Observador removido. Total observadores: " + Fachada.getInstancia().getObservadores().size());
    }

    /** Implementación del patrón Observador: recibe eventos globales de la Fachada. */
    @Override
    public void actualizar(Object evento, Observable origen) {
        System.out.println("🔔 ControladorAdmin.actualizar() recibió evento: " + evento + " | Conexión SSE: " + (conexionNavegador != null && conexionNavegador.estaConectado()));
        
        if(!(evento instanceof Fachada.Eventos)) return;
        Fachada.Eventos ev = (Fachada.Eventos) evento;
        switch (ev) {
            case transitoRegistrado -> enviarActualizacionTransitos();
            default -> { }
        }
    }

    private void enviarActualizacionTransitos(){
        System.out.println("📤 Intentando enviar actualización de tránsitos...");
        if(conexionNavegador == null || !conexionNavegador.estaConectado()) {
            System.out.println("⚠️ No hay conexión SSE activa para este controlador");
            return;
        }
        // Por simplicidad se envía la cantidad total de tránsitos
        int total = Fachada.getInstancia().getTransitos().size();
        System.out.println("✅ Enviando total tránsitos: " + total);
        conexionNavegador.enviarJSON(Respuesta.lista(new Respuesta("totalTransitos", total)));
    }
}