package obligatorio.obligatorio.observador;

import java.util.ArrayList;
import java.util.List;

public class Observable {

    private final ArrayList<Observador> observadores = new ArrayList<>();

    public void agregarObservador(Observador obs){
        if(!observadores.contains(obs)){
            observadores.add(obs);
        }
    }
    public void quitarObservador(Observador obs){
        
        observadores.remove(obs);
        
    }
    
    public List<Observador> getObservadores() {
        return new ArrayList<>(observadores);
    }
    
    public void avisar(Object evento){

        ArrayList<Observador> copia = new ArrayList<>(observadores);
        for(Observador obs:copia){
            obs.actualizar(this, evento);
        }

    }

}
