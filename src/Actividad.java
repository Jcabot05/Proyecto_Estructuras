import java.time.LocalDate;


public class Actividad {
    private String nombre;
    private LocalDate fechaLimite;
    private String descripcion;


    public Actividad(String nombre, LocalDate fechaLimite) {
        this.nombre = nombre;
        this.fechaLimite = fechaLimite;
        this.descripcion = "";
    }

    public Actividad(String nombre, LocalDate fechaLimite, String descripcion) {
        this.nombre = nombre;
        this.fechaLimite = fechaLimite;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public boolean yaFenecio() {
        return LocalDate.now().isAfter(fechaLimite);
    }

    @Override
    public String toString() {
        String estado = yaFenecio() ? " [VENCIDA]" : "";
        return nombre + " - Límite: " + fechaLimite + estado;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Actividad that = (Actividad) obj;
        return nombre.equals(that.nombre);
    }
}
