import java.util.Comparator;

public class compararEntregasxNotas implements Comparator<Entrega> {
    @Override
    public int compare(Entrega a, Entrega b) {
        if (a.getNota() == b.getNota()) return 0;
        if (a.getNota() > b.getNota()) return 1;
        return -1;
    }
}
