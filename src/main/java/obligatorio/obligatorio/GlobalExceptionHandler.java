package obligatorio.obligatorio;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import obligatorio.obligatorio.Modelo.ObligatorioException;



@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(ObligatorioException.class)
    public ResponseEntity<String> manejarException(ObligatorioException ex) {
        
       return ResponseEntity.status(299).body(ex.getMessage());
    }
}
