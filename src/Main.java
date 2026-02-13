import java.time.LocalDate;
import java.util.Comparator;


class CompararEntregasPorNotas implements Comparator<Entrega> {
    @Override
    public int compare(Entrega a, Entrega b) {
        if (a.getNota() == b.getNota()) return 0;
        if (a.getNota() > b.getNota()) return 1;
        return -1;
    }
}

public class Main {
    
    public static void main(String[] args) {

        System.out.println("SISTEMA DE GESTIÓN DE CURSO");

        // 1. CREAR Y CARGAR DATOS DE ESTUDIANTES

        ListaCompuesta<Estudiante, Entrega> listaEstudiantes = new ListaCompuesta<>();
        NodoCompuesto<Estudiante, Entrega> p;

        // Agregar estudiantes con sus entregas
        listaEstudiantes.add(p = new NodoCompuesto<>(new Estudiante("Pepito", "Abad")));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(30));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(50));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(40));

        listaEstudiantes.add(p = new NodoCompuesto<>(new Estudiante("Luisito", "Comunica")));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(20));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(10));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(90));

        listaEstudiantes.add(p = new NodoCompuesto<>(new Estudiante("Josecito", "Noboa")));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(6));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(10));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(100));

        listaEstudiantes.add(p = new NodoCompuesto<>(new Estudiante("Maria", "Garcia")));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(85));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(85));  // Misma nota
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(90));

        listaEstudiantes.add(p = new NodoCompuesto<>(new Estudiante("Carlos", "Rodriguez")));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(70));
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(70));  // Misma nota
        listaEstudiantes.addElementInSecondaryList(p, new Entrega(75));

        System.out.println("Lista completa de estudiantes:");
        System.out.println(listaEstudiantes);

        // 2. CREAR Y CARGAR ACTIVIDADES
        ListaCompuesta<Actividad, Estudiante> listaActividades = new ListaCompuesta<>();

        listaActividades.add(new Actividad("Tarea 1", LocalDate.of(2025, 1, 15)));
        listaActividades.add(new Actividad("Proyecto", LocalDate.of(2025, 1, 30)));
        listaActividades.add(new Actividad("Examen", LocalDate.of(2025, 3, 15)));
        listaActividades.add(new Actividad("Taller 1", LocalDate.of(2025, 2, 20)));

        System.out.println("Lista de actividades:");
        for (NodoCompuesto<Actividad, Estudiante> nodo = listaActividades.getHeader();
             nodo != null;
             nodo = nodo.getNext()) {
            System.out.println("  - " + nodo.getData());
        }

        // 3. probando buscar con comparadores
        Entrega eBuscar = new Entrega(15);
        System.out.println("Buscando estudiantes con entregas menores a: " + eBuscar.getNota());

        ListaCompuesta<Estudiante, Entrega> estudiantesMenores =
                listaEstudiantes.buscarTodosMenoresEnListaSecundaria(
                        new CompararEntregasPorNotas(), eBuscar);

        System.out.println("Estudiantes encontrados:");
        System.out.println(estudiantesMenores);

        // Use las funciones del TDA ListaCompuesta para crear funciones que permitan las siguientes consultas a los datos cargados en las estructuras. Pruebe estas funciones en un main. Ojo, esta parte debe ser dividida entre miembros del grupo y debe indicarse quien hizo que función.
        //Actividades del curso
        //Cuya fecha de entrega límite ya feneció.
        //Estudiantes del curso
        //Que tienen la misma nota en dos actividades diferentes
        System.out.println("");
        System.out.println("CONSULTAS REQUERIDAS");
        System.out.println("");

        // Consulta 1: Actividades vencidas;
        // Hecha por Stefano Jeremias Cabot
        System.out.println("Estudiantes con actividades vencidas:");
        ListaCompuesta<Actividad, Estudiante> actividadesVencidas =
                Consultas.actividadesVencidas(listaActividades);

        if (actividadesVencidas.isEmpty()) {
            System.out.println("  No hay actividades vencidas");
        } else {
            for (NodoCompuesto<Actividad, Estudiante> nodo = actividadesVencidas.getHeader();
                 nodo != null;
                 nodo = nodo.getNext()) {
                System.out.println("  - " + nodo.getData());
            }
        }

        // Consulta extra: Todos los estudiantes en el curso
        ListaCompuesta<Estudiante, Entrega> todosEstudiantes =
                Consultas.obtenerTodosEstudiantes(listaEstudiantes);

        System.out.println("Total de estudiantes en el curso: " + todosEstudiantes.getSize());
        for (NodoCompuesto<Estudiante, Entrega> nodo = todosEstudiantes.getHeader();
             nodo != null;
             nodo = nodo.getNext()) {
            System.out.println("  - " + nodo.getData());
        }

        // Consulta 2: Estudiantes con la misma nota en dos actividades
        // Hecha por Jose Hidalgo
        //
        System.out.println("Estudiantes que tienen la misma nota en la misma actividad: ");
        ListaCompuesta<Estudiante, Entrega> estudiantesNotasIguales =
                Consultas.estudiantesConMismaNota(listaEstudiantes);

        if (estudiantesNotasIguales.isEmpty()) {
            System.out.println("No hay estudiantes con notas iguales en dos actividades");
        } else {
            for (NodoCompuesto<Estudiante, Entrega> nodo = estudiantesNotasIguales.getHeader();
                 nodo != null;
                 nodo = nodo.getNext()) {
                System.out.println("  - " + nodo.getData());
            }
        }

        // 5. ESTADÍSTICAS DE ESTUDIANTES

        System.out.println("");
        System.out.println("Estadísticas de los estudiantes: ");
        System.out.println("");

        for (NodoCompuesto<Estudiante, Entrega> nodo = listaEstudiantes.getHeader();
             nodo != null;
             nodo = nodo.getNext()) {
            Consultas.imprimirEstadisticas(nodo);
        }
    }
}
