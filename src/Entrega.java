import java.time.LocalDate;

/**
 * Representa la entrega de un estudiante para una actividad.
 *
 * Campos clave:
 *   - nombreActividad  : actividad a la que pertenece esta entrega
 *   - nombreEstudiante : estudiante que entrego (util en la lista de actividades)
 *   - nota             : calificacion numerica (solo valida si calificado == true)
 *   - calificado       : boolean que indica si se asigno una nota (SEPARADO de la nota)
 *   - fechaEntrega     : cuando el estudiante envio; null = no ha enviado todavia
 *
 * La separacion entre 'nota' y 'calificado' permite representar tres estados:
 *   1. No enviada  : fechaEntrega == null, calificado == false
 *   2. Enviada S/C : fechaEntrega != null, calificado == false
 *   3. Calificada  : fechaEntrega != null, calificado == true, nota = valor real
 */
public class Entrega {

    private String    nombreActividad;
    private String    nombreEstudiante;
    private int       nota;        // valor de la nota (irrelevante si calificado == false)
    private boolean   calificado;  // true solo cuando se asigno una nota valida
    private LocalDate fechaEntrega; // null si aun no fue enviada

    // =========================================================
    // Constructores
    // =========================================================

    /**
     * Crea una entrega SIN nota asignada todavia.
     * El campo calificado queda en false independientemente de lo demas.
     *
     * @param nombreActividad  nombre de la actividad
     * @param nombreEstudiante nombre del estudiante
     * @param fechaEntrega     fecha de envio; null si no se ha enviado aun
     */
    public Entrega(String nombreActividad, String nombreEstudiante,
                   LocalDate fechaEntrega) {
        this.nombreActividad  = nombreActividad;
        this.nombreEstudiante = nombreEstudiante;
        this.fechaEntrega     = fechaEntrega;
        this.nota             = 0;     // valor por defecto, no usado hasta calificar
        this.calificado       = false; // explicito: aun no calificada
    }

    /**
     * Crea una entrega CON nota ya asignada.
     * El campo calificado se marca en true automaticamente.
     *
     * @param nombreActividad  nombre de la actividad
     * @param nombreEstudiante nombre del estudiante
     * @param fechaEntrega     fecha de envio
     * @param nota             calificacion asignada
     */
    public Entrega(String nombreActividad, String nombreEstudiante,
                   LocalDate fechaEntrega, int nota) {
        this.nombreActividad  = nombreActividad;
        this.nombreEstudiante = nombreEstudiante;
        this.fechaEntrega     = fechaEntrega;
        this.nota             = nota;
        this.calificado       = true;  // explicito: ya tiene nota
    }

    // =========================================================
    // Consultas de estado
    // =========================================================

    /**
     * Indica si esta entrega tiene una calificacion asignada.
     * Este boolean es independiente del valor de 'nota'.
     */
    public boolean estaCalificada() {
        return calificado;
    }

    /**
     * Indica si el estudiante envio la entrega (aunque no este calificada).
     * Una entrega enviada tiene fechaEntrega != null.
     */
    public boolean fueEnviada() {
        return fechaEntrega != null;
    }

    /**
     * Indica si la entrega fue enviada despues de la fecha dada.
     */
    public boolean enviadadespuesDe(LocalDate fecha) {
        return fechaEntrega != null && fechaEntrega.isAfter(fecha);
    }

    // =========================================================
    // Getters
    // =========================================================

    public String    getNombreActividad()  { return nombreActividad; }
    public String    getNombreEstudiante() { return nombreEstudiante; }
    public int       getNota()             { return nota; }
    public boolean   isCalificado()        { return calificado; }
    public LocalDate getFechaEntrega()     { return fechaEntrega; }

    // =========================================================
    // Setters
    // =========================================================

    /**
     * Asigna una nota y marca calificado = true.
     * Estos dos pasos siempre van juntos para mantener coherencia.
     */
    public void calificar(int nota) {
        this.nota       = nota;
        this.calificado = true;
    }

    public void setFechaEntrega(LocalDate fecha) {
        this.fechaEntrega = fecha;
    }

    // =========================================================
    // Object overrides
    // =========================================================

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
