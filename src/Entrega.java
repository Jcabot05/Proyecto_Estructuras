public class Entrega {
    private int nota;
    private String nombreActividad;

    public Entrega(int nota) {
        this.nota = nota;
        this.nombreActividad = "";
    }

    public Entrega(int nota, String nombreActividad) {
        this.nota = nota;
        this.nombreActividad = nombreActividad;
    }
    
    // Getters
    public int getNota() {
        return nota;
    }
    
    public String getNombreActividad() {
        return nombreActividad;
    }
    
    // Setters
    public void setNota(int nota) {
        this.nota = nota;
    }
    
    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }
    
    @Override
    public String toString() {
        if (nombreActividad.isEmpty()) {
            return "**" + this.nota + "**";
        }
        return nombreActividad + ": **" + this.nota + "**";
    }
}
