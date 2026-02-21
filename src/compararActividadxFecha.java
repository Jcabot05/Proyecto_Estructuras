import java.util.Comparator;

public class compararActividadxFecha implements Comparator<Actividad> {
    @Override
    public int compare(Actividad a, Actividad b) {
        if (a.getFechaEnvio().isBefore(b.getFechaEnvio())) return -1;
        if (a.getFechaEnvio().isAfter(b.getFechaEnvio())) return 1;
        return 0;
    }
}
