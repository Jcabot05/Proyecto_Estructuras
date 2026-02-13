import java.time.LocalDate;

public class Actividad {
    private String nombre;
    private LocalDate fechaLimite;

    public Actividad(String nombre, LocalDate fechaLimite) {
        this.nombre = nombre;
        this.fechaLimite = fechaLimite;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }
    
    public LocalDate getFechaLimite() {
        return fechaLimite;
    }
    
    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
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
