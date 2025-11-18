package obligatorio.obligatorio.Modelo.modelos;

public class EstadoPropietarioHabilitado extends EstadoPropietario {
    public EstadoPropietarioHabilitado(Propietario propietario) {
        super(propietario, "Habilitado");
    }

    @Override
    public void asignarBonificacion(Bonificacion bonificacion, Puesto puesto) throws ObligatorioException {
        if (bonificacion == null) {
            throw new ObligatorioException("Debe especificar una bonificación");
        }
        if (puesto == null) {
            throw new ObligatorioException("Debe especificar un puesto");
        }
        if (propietario.tieneBonificacionParaPuesto(puesto)) {
            throw new ObligatorioException("Ya tiene una bonificación asignada para ese puesto");
        }



        //PREGUNTA A PRFE: ESTO ROMPE EXPERTO?? Entiendo que no porque es el propietario mismo que se lo asigna
        AsignacionBonificacion asignacion = new AsignacionBonificacion(propietario, puesto, bonificacion, java.time.LocalDate.now());
        propietario.agregarAsignacionBonificacion(asignacion);
    }
}
