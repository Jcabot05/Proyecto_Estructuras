import java.time.LocalDate;

// Entrega simple
public class Entrega {

    private String    nombreActividad;
    private String    nombreEstudiante;
    private int       nota;
    private boolean   calificado;
    private LocalDate fechaEntrega;

    public Entrega(int nota) {
        this.nombreActividad = "";
        this.nombreEstudiante = "";
        this.fechaEntrega = null;
        this.nota = nota;
        this.calificado = true;
    }

    public Entrega(String nombreActividad, String nombreEstudiante,
                   LocalDate fechaEntrega) {
        this.nombreActividad  = nombreActividad;
        this.nombreEstudiante = nombreEstudiante;
        this.fechaEntrega     = fechaEntrega;
        this.nota             = 0;
        this.calificado       = false;
    }

    public Entrega(String nombreActividad, String nombreEstudiante,
                   LocalDate fechaEntrega, int nota) {
        this.nombreActividad  = nombreActividad;
        this.nombreEstudiante = nombreEstudiante;
        this.fechaEntrega     = fechaEntrega;
        this.nota             = nota;
        this.calificado       = true;
    }

    public boolean estaCalificada() {
        return calificado;
    }

    public boolean fueEnviada() {
        return fechaEntrega != null;
    }

    public boolean enviadadespuesDe(LocalDate fecha) {
        return fechaEntrega != null && fechaEntrega.isAfter(fecha);
    }

    public String    getNombreActividad()  { return nombreActividad; }
    public String    getNombreEstudiante() { return nombreEstudiante; }
    public int       getNota()             { return nota; }
    public boolean   isCalificado()        { return calificado; }
    public LocalDate getFechaEntrega()     { return fechaEntrega; }

    public void calificar(int nota) {
        this.nota       = nota;
        this.calificado = true;
    }

    public void setFechaEntrega(LocalDate fecha) {
        this.fechaEntrega = fecha;
    }

    @Override
    public String toString() {
        String estado;
        if (!fueEnviada()) {
            estado = "no enviada";
        } else if (!calificado) {
            estado = "enviada, sin calificar";
        } else {
            estado = "nota: " + nota;
        }
        return "[" + nombreActividad + " | " + nombreEstudiante
                + " | " + estado
                + (fechaEntrega != null ? " | " + fechaEntrega : "") + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entrega that = (Entrega) obj;
        return nombreActividad.equals(that.nombreActividad)
                && nombreEstudiante.equals(that.nombreEstudiante);
    }

    @Override
    public int hashCode() {
        return (nombreActividad + nombreEstudiante).hashCode();
    }
}
