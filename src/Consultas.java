import java.time.LocalDate;

public class Consultas {
    public static ListaCompuesta<Actividad, Estudiante> actividadesVencidas(
            ListaCompuesta<Actividad, Estudiante> actividades) {
        
        ListaCompuesta<Actividad, Estudiante> vencidas = new ListaCompuesta<>();
        
        for (NodoCompuesto<Actividad, Estudiante> nodo = actividades.getHeader(); 
             nodo != null; 
             nodo = nodo.getNext()) {
            
            Actividad act = nodo.getData();
            if (act.yaFenecio()) {

                vencidas.add(act);
            }
        }
        
        return vencidas;
    }

    public static ListaCompuesta<Estudiante, Entrega> obtenerTodosEstudiantes(
            ListaCompuesta<Estudiante, Entrega> estudiantes) {
        
        // Ya está en el formato correcto, solo retornamos una copia de referencia
        return estudiantes;
    }

    public static ListaCompuesta<Estudiante, Entrega> estudiantesConMismaNota(
            ListaCompuesta<Estudiante, Entrega> estudiantes) {
        
        ListaCompuesta<Estudiante, Entrega> resultado = new ListaCompuesta<>();
        
        for (NodoCompuesto<Estudiante, Entrega> nodo = estudiantes.getHeader(); 
             nodo != null; 
             nodo = nodo.getNext()) {
            
            ListaCompuesta<Estudiante, Entrega> entregas = nodo.getReferenciaLista();
            
            if (entregas != null && entregas.getSize() >= 2) {
                // Verificar si hay al menos dos entregas con la misma nota
                boolean tieneNotasIguales = false;
                
                for (NodoCompuesto<Estudiante, Entrega> e1 = entregas.getHeader(); 
                     e1 != null && !tieneNotasIguales; 
                     e1 = e1.getNext()) {
                    
                    // Cast a Entrega (los datos en la lista secundaria son Entregas almacenadas como E)
                    Entrega entrega1 = (Entrega) (Object) e1.getData();
                    
                    for (NodoCompuesto<Estudiante, Entrega> e2 = e1.getNext(); 
                         e2 != null; 
                         e2 = e2.getNext()) {
                        
                        Entrega entrega2 = (Entrega) (Object) e2.getData();
                        
                        if (entrega1.getNota() == entrega2.getNota()) {
                            tieneNotasIguales = true;
                            break;
                        }
                    }
                }
                
                if (tieneNotasIguales) {
                    resultado.add(nodo.getData());
                }
            }
        }
        
        return resultado;
    }

    public static double calcularPromedio(NodoCompuesto<Estudiante, Entrega> nodoEstudiante) {
        ListaCompuesta<Estudiante, Entrega> entregas = nodoEstudiante.getReferenciaLista();
        
        if (entregas == null || entregas.isEmpty()) {
            return 0.0;
        }
        
        int suma = 0;
        int contador = 0;
        
        for (NodoCompuesto<Estudiante, Entrega> e = entregas.getHeader(); 
             e != null; 
             e = e.getNext()) {
            
            Entrega entrega = (Entrega) (Object) e.getData();
            suma += entrega.getNota();
            contador++;
        }
        
        return contador > 0 ? (double) suma / contador : 0.0;
    }

    public static int notaMaxima(NodoCompuesto<Estudiante, Entrega> nodoEstudiante) {
        ListaCompuesta<Estudiante, Entrega> entregas = nodoEstudiante.getReferenciaLista();
        
        if (entregas == null || entregas.isEmpty()) {
            return 0;
        }
        
        int max = Integer.MIN_VALUE;
        
        for (NodoCompuesto<Estudiante, Entrega> e = entregas.getHeader(); 
             e != null; 
             e = e.getNext()) {
            
            Entrega entrega = (Entrega) (Object) e.getData();
            if (entrega.getNota() > max) {
                max = entrega.getNota();
            }
        }
        
        return max;
    }

    public static int notaMinima(NodoCompuesto<Estudiante, Entrega> nodoEstudiante) {
        ListaCompuesta<Estudiante, Entrega> entregas = nodoEstudiante.getReferenciaLista();
        
        if (entregas == null || entregas.isEmpty()) {
            return 0;
        }
        
        int min = Integer.MAX_VALUE;
        
        for (NodoCompuesto<Estudiante, Entrega> e = entregas.getHeader(); 
             e != null; 
             e = e.getNext()) {
            
            Entrega entrega = (Entrega) (Object) e.getData();
            if (entrega.getNota() < min) {
                min = entrega.getNota();
            }
        }
        
        return min;
    }

    public static void imprimirEstadisticas(NodoCompuesto<Estudiante, Entrega> nodoEstudiante) {
        Estudiante est = nodoEstudiante.getData();
        System.out.println("Estadísticas de " + est + "");
        System.out.println("Promedio: " + String.format("%.2f", calcularPromedio(nodoEstudiante)));
        System.out.println("Nota máxima: " + notaMaxima(nodoEstudiante));
        System.out.println("Nota mínima: " + notaMinima(nodoEstudiante));
        
        ListaCompuesta<Estudiante, Entrega> entregas = nodoEstudiante.getReferenciaLista();
        System.out.println("Total de entregas: " + (entregas != null ? entregas.getSize() : 0));
    }
}
