package obligatorio.obligatorio.Controladores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import obligatorio.obligatorio.Modelo.Administrador;
import obligatorio.obligatorio.Modelo.Fachada;
import obligatorio.obligatorio.Modelo.ObligatorioException;
import obligatorio.obligatorio.Modelo.Sesion;

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

    @PostMapping("/cargarMonitor")
    public Object cargarMonitor(HttpSession sesionHttp) {
        try {
            Administrador admin = administradorEnSesion(sesionHttp);
            
            // Obtener sesiones activas
            List<Sesion> sesiones = Fachada.getInstancia().getSesiones();
            
            // Convertir a DTOs simples
            List<Map<String, Object>> sesionesDTO = sesiones.stream()
                .map(s -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("nombrePropietario", s.getPropietario().getNombreCompleto());
                    dto.put("cedula", s.getPropietario().getCedula());
                    dto.put("fechaIngreso", s.getFechaIngreso());
                    return dto;
                })
                .collect(Collectors.toList());

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalPropietarios", 
                Fachada.getInstancia().getSesiones().size()); 
            estadisticas.put("totalTransitos", 
                Fachada.getInstancia().getTransitos().size());

            return Respuesta.lista(
                new Respuesta("infoAdmin", admin.getNombreCompleto()),
                new Respuesta("sesionesActivas", sesionesDTO),
                new Respuesta("estadisticas", estadisticas)
            );
            
        } catch (ObligatorioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}