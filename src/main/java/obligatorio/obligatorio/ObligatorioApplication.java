package obligatorio.obligatorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import obligatorio.obligatorio.Modelo.modelos.ObligatorioException;

@SpringBootApplication
public class ObligatorioApplication {

	public static void main(String[] args) throws ObligatorioException{
		SpringApplication.run(ObligatorioApplication.class, args);
		// Registrar datos precargados en los sistemas
		obligatorio.obligatorio.Modelo.fachada.Fachada.getInstancia().registrarDatosPrecargados();
	}


}
