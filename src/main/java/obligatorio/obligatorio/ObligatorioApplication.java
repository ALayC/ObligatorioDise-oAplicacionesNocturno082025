package obligatorio.obligatorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import obligatorio.obligatorio.Modelo.PrecargaDatos;

@SpringBootApplication
public class ObligatorioApplication {

	public static void main(String[] args) {
		 PrecargaDatos.crear();
		SpringApplication.run(ObligatorioApplication.class, args);
	}

}
