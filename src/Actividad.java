import java.time.LocalDate;

// Actividad sencilla
public class Actividad {

    private String nombre;
    private LocalDate fechaEnvio;
    private LocalDate fechaLimite;
    private int totalEstudiantesCurso;

    public Actividad(String nombre, LocalDate fechaLimite) {
        this.nombre = nombre;
        this.fechaEnvio = fechaLimite;
        this.fechaLimite = fechaLimite;
        this.totalEstudiantesCurso = 0;
    }

    public Actividad(String nombre, LocalDate fechaEnvio, LocalDate fechaLimite) {
        this.nombre = nombre;
        this.fechaEnvio = fechaEnvio;
        this.fechaLimite = fechaLimite;
        this.totalEstudiantesCurso = 0;
    }

    public boolean yaFenecio() {
        return LocalDate.now().isAfter(fechaLimite);
    }

    public String getNombre()          { return nombre; }
    public LocalDate getFechaEnvio()   { return fechaEnvio; }
    public LocalDate getFechaLimite()  { return fechaLimite; }
    public int getTotalEstudiantesCurso() { return totalEstudiantesCurso; }

    public void setNombre(String nombre)               { this.nombre = nombre; }
    public void setFechaEnvio(LocalDate fechaEnvio)    { this.fechaEnvio = fechaEnvio; }
    public void setFechaLimite(LocalDate fechaLimite)  { this.fechaLimite = fechaLimite; }
    public void setTotalEstudiantesCurso(int total)    { this.totalEstudiantesCurso = total; }

    @Override
    public String toString() {
        return nombre + " envio:" + fechaEnvio + " limite:" + fechaLimite;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Actividad that = (Actividad) obj;
        return nombre.equals(that.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}
