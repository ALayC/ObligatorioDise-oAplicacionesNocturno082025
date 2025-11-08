package obligatorio.obligatorio.Controladores;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.DTO.PuestoDTO;
import obligatorio.obligatorio.DTO.ResultadoEmulacionDTO;
import obligatorio.obligatorio.DTO.TarifaDTO;
import obligatorio.obligatorio.Modelo.modelos.Administrador;
import obligatorio.obligatorio.Modelo.fachada.Fachada;
import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;

@RestController
@RequestMapping("/admin")
public class ControladorAdmin {

    // Helper para validar sesión
    private Administrador administradorEnSesion(HttpSession sesionHttp) throws ObligatorioException {
        Object obj = sesionHttp.getAttribute("usuarioAdmin");
        if (obj instanceof Administrador a) {
            return a;
        }
        throw new ObligatorioException("Sesión expirada");
    }

    @PostMapping("/cargarEmulador")
    public Object cargarEmulador(HttpSession sesionHttp) {
        try {
            Administrador admin = administradorEnSesion(sesionHttp);
            
            // Obtener lista de puestos como DTOs desde el sistema
            List<PuestoDTO> puestosDTO = Fachada.getInstancia().getPuestosDTO();

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
}