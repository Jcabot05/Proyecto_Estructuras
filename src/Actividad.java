import java.time.LocalDate;

/**
 * Representa una actividad del curso (tarea, examen, proyecto, etc.).
 *
 * Guarda el nombre, la fecha limite y el total de estudiantes del curso
 * (necesario para detectar entregas incompletas).
 */
public class Actividad {

    private String nombre;
    private LocalDate fechaLimite;
    private int totalEstudiantesCurso; // se asigna despues de cargar todos los estudiantes

    // =========================================================
    // Constructores
    // =========================================================

    public Actividad(String nombre, LocalDate fechaLimite) {
        this.nombre = nombre;
        this.fechaLimite = fechaLimite;
        this.totalEstudiantesCurso = 0;
    }

    // =========================================================
    // Consultas de estado
    // =========================================================

    /**
     * Indica si la fecha limite de la actividad ya paso (es anterior a hoy).
     */
    public boolean yaFenecio() {
        return LocalDate.now().isAfter(fechaLimite);
    }

    // =========================================================
    // Getters / Setters
    // =========================================================

    public String getNombre()          { return nombre; }
    public LocalDate getFechaLimite()  { return fechaLimite; }
    public int getTotalEstudiantesCurso() { return totalEstudiantesCurso; }

    public void setNombre(String nombre)               { this.nombre = nombre; }
    public void setFechaLimite(LocalDate fechaLimite)  { this.fechaLimite = fechaLimite; }
    public void setTotalEstudiantesCurso(int total)    { this.totalEstudiantesCurso = total; }

    @Override
    public String toString() {
        String estado = yaFenecio() ? " [VENCIDA]" : " [ACTIVA]";
        return nombre + " | Limite: " + fechaLimite + estado;
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
