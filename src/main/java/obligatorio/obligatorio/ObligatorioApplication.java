package obligatorio.obligatorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import obligatorio.obligatorio.Modelo.ObligatorioException;
import obligatorio.obligatorio.Modelo.PrecargaDatos;

@SpringBootApplication
public class ObligatorioApplication {

	public static void main(String[] args) throws ObligatorioException{		 
		SpringApplication.run(ObligatorioApplication.class, args);
		 PrecargaDatos.crear();
	}

}
