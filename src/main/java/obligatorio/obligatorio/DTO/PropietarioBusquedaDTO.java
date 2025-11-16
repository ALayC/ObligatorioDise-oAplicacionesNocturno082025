package obligatorio.obligatorio.DTO;


    public  class PropietarioBusquedaDTO {
        public String cedula;
        public String nombreCompleto;
        public String estado;

        public PropietarioBusquedaDTO(String cedula, String nombreCompleto, String estado) {
            this.cedula = cedula;
            this.nombreCompleto = nombreCompleto;
            this.estado = estado;
        }
    }