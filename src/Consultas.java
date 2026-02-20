import java.time.LocalDate;

/**
 * Clase de consultas del sistema de Libro de Calificaciones.
 *
 * Cada metodo usa las operaciones del TDA ListaCompuesta o ListaSimple
 * para responder a una consulta especifica del enunciado (seccion 3.3).
 *
 * Las consultas estan agrupadas por categoria:
 *   A) Consultas sobre Actividades
 *   B) Consultas sobre Entregas
 *   C) Consultas sobre Estudiantes
 *   D) Consultas sobre Calculos
 *   E) Estadisticas individuales
 */
public class Consultas {

    // =========================================================
    // A) CONSULTAS SOBRE ACTIVIDADES
    // =========================================================

    /**
     * A1 - Actividades cuya fecha limite ya fenecio (es anterior a hoy).
     */
    public static ListaCompuesta<Actividad, Entrega> actividadesVencidas(
            ListaCompuesta<Actividad, Entrega> actividades) {

        ListaCompuesta<Actividad, Entrega> resultado = new ListaCompuesta<>();
        for (NodoCompuesto<Actividad, Entrega> nodo = actividades.getHeader();
             nodo != null; nodo = nodo.getNext()) {
            if (nodo.getData().yaFenecio()) {
                NodoCompuesto<Actividad, Entrega> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(nodo.getReferenciaLista());
                resultado.add(copia);
            }
        }
        return resultado;
    }

    /**
     * A2 - Actividades con entregas incompletas.
     * Retorna actividades donde el numero de entregas enviadas < total estudiantes.
     */
    public static ListaCompuesta<Actividad, Entrega> actividadesConEntregasIncompletas(
            ListaCompuesta<Actividad, Entrega> actividades,
            int totalEstudiantes) {

        ListaCompuesta<Actividad, Entrega> resultado = new ListaCompuesta<>();

        for (NodoCompuesto<Actividad, Entrega> nodo = actividades.getHeader();
             nodo != null; nodo = nodo.getNext()) {

            ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
            int enviadas = 0;
            if (sublista != null) {
                // Contar cuantas entregas realmente fueron enviadas
                ListaSimple<Entrega>.Iterador it = sublista.iterador();
                while (it.hasNext()) {
                    if (it.next().fueEnviada()) enviadas++;
                }
            }

            if (enviadas < totalEstudiantes) {
                NodoCompuesto<Actividad, Entrega> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(sublista);
                resultado.add(copia);
            }
        }
        return resultado;
    }

    /**
     * A3 - Actividades en las que ALGUNA calificacion es menor a un valor dado.
     */
    public static ListaCompuesta<Actividad, Entrega> actividadesConNotaMenorA(
            ListaCompuesta<Actividad, Entrega> actividades, int umbral) {

        ListaCompuesta<Actividad, Entrega> resultado = new ListaCompuesta<>();
        for (NodoCompuesto<Actividad, Entrega> nodo = actividades.getHeader();
             nodo != null; nodo = nodo.getNext()) {
            ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
            if (sublista == null) continue;
            
            // Verificar si alguna entrega esta calificada con nota menor al umbral
            boolean encontrado = false;
            ListaSimple<Entrega>.Iterador it = sublista.iterador();
            while (it.hasNext()) {
                Entrega e = it.next();
                if (e.estaCalificada() && e.getNota() < umbral) {
                    encontrado = true;
                    break;
                }
            }
            
            if (encontrado) {
                NodoCompuesto<Actividad, Entrega> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(sublista);
                resultado.add(copia);
            }
        }
        return resultado;
    }

    // =========================================================
    // B) CONSULTAS SOBRE ENTREGAS
    // =========================================================

    /**
     * B1 - Entregas de actividades enviadas despues de una fecha dada
     *      que aun NO han recibido calificacion.
     *
     * Retorna una ListaCompuesta donde cada nodo es una actividad y su lista
     * secundaria contiene solo las entregas que cumplen el criterio.
     */
    public static ListaCompuesta<Actividad, Entrega> entregasTardiasNoCalificadas(
            ListaCompuesta<Actividad, Entrega> actividades, LocalDate fecha) {

        ListaCompuesta<Actividad, Entrega> resultado = new ListaCompuesta<>();

        for (NodoCompuesto<Actividad, Entrega> nodo = actividades.getHeader();
             nodo != null; nodo = nodo.getNext()) {

            ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
            if (sublista == null) continue;

            // Crear lista con solo las entregas tardias sin calificar
            ListaSimple<Entrega> filtradas = new ListaSimple<>();
            ListaSimple<Entrega>.Iterador it = sublista.iterador();
            while (it.hasNext()) {
                Entrega e = it.next();
                if (e.enviadadespuesDe(fecha) && !e.estaCalificada()) {
                    filtradas.add(e);
                }
            }

            if (!filtradas.isEmpty()) {
                NodoCompuesto<Actividad, Entrega> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(filtradas);
                resultado.add(copia);
            }
        }
        return resultado;
    }

    // =========================================================
    // C) CONSULTAS SOBRE ESTUDIANTES
    // =========================================================

    /**
     * C1 - Estudiantes cuyo porcentaje de entregas enviadas es mayor al porcentaje dado.
     *
     * @param porcentaje valor entre 0 y 100
     * @param totalActividades numero total de actividades del curso
     */
    public static ListaCompuesta<Estudiante, Entrega> estudiantesConPorcentajeMayorA(
            ListaCompuesta<Estudiante, Entrega> estudiantes,
            int totalActividades, double porcentaje) {

        ListaCompuesta<Estudiante, Entrega> resultado = new ListaCompuesta<>();

        for (NodoCompuesto<Estudiante, Entrega> nodo = estudiantes.getHeader();
             nodo != null; nodo = nodo.getNext()) {

            ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
            if (sublista == null) continue;

            // Contar cuantas entregas realmente fueron enviadas
            int enviadas = 0;
            ListaSimple<Entrega>.Iterador it = sublista.iterador();
            while (it.hasNext()) {
                if (it.next().fueEnviada()) enviadas++;
            }
            
            double pct = totalActividades > 0
                    ? (enviadas * 100.0 / totalActividades)
                    : 0.0;

            if (pct > porcentaje) {
                NodoCompuesto<Estudiante, Entrega> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(sublista);
                resultado.add(copia);
            }
        }
        return resultado;
    }

    /**
     * C2 - Estudiantes que no respondieron actividades vencidas.
     * Retorna estudiantes que tienen al menos UNA actividad ya vencida
     * para la cual no registraron ninguna entrega enviada.
     */
    public static ListaCompuesta<Estudiante, Entrega> estudiantesSinEntregasVencidas(
            ListaCompuesta<Estudiante, Entrega> estudiantes,
            ListaCompuesta<Actividad, Entrega>  actividades) {

        // Reunir nombres de actividades vencidas
        ListaSimple<String> nombresVencidas = new ListaSimple<>();
        for (NodoCompuesto<Actividad, Entrega> nodo = actividades.getHeader();
             nodo != null; nodo = nodo.getNext()) {
            if (nodo.getData().yaFenecio()) {
                nombresVencidas.add(nodo.getData().getNombre());
            }
        }

        ListaCompuesta<Estudiante, Entrega> resultado = new ListaCompuesta<>();

        for (NodoCompuesto<Estudiante, Entrega> nodo = estudiantes.getHeader();
             nodo != null; nodo = nodo.getNext()) {

            ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
            if (sublista == null) {
                resultado.add(new NodoCompuesto<>(nodo.getData()));
                continue;
            }

            // Verificar si alguna entrega de actividad vencida no fue enviada
            boolean tienePendiente = false;
            ListaSimple<String>.Iterador it = nombresVencidas.iterador();
            while (it.hasNext()) {
                String nombreAct = it.next();
                // Buscar la entrega de esa actividad
                ListaSimple<Entrega>.Iterador itE = sublista.iterador();
                boolean enviada = false;
                while (itE.hasNext()) {
                    Entrega e = itE.next();
                    if (e.getNombreActividad().equals(nombreAct) && e.fueEnviada()) {
                        enviada = true;
                        break;
                    }
                }
                if (!enviada) {
                    tienePendiente = true;
                    break;
                }
            }

            if (tienePendiente) {
                NodoCompuesto<Estudiante, Entrega> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(sublista);
                resultado.add(copia);
            }
        }
        return resultado;
    }

    /**
     * C3 - Estudiantes que tienen la misma nota en DOS actividades diferentes.
     */
    public static ListaCompuesta<Estudiante, Entrega> estudiantesConMismaNota(
            ListaCompuesta<Estudiante, Entrega> estudiantes) {

        ListaCompuesta<Estudiante, Entrega> resultado = new ListaCompuesta<>();

        for (NodoCompuesto<Estudiante, Entrega> nodo = estudiantes.getHeader();
             nodo != null; nodo = nodo.getNext()) {

            ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
            if (sublista == null || sublista.getSize() < 2) continue;

            boolean encontrado = false;

            // Comparar cada par de entregas calificadas
            for (int i = 0; i < sublista.getSize() && !encontrado; i++) {
                Entrega e1 = sublista.get(i);
                if (!e1.estaCalificada()) continue;

                for (int j = i + 1; j < sublista.getSize() && !encontrado; j++) {
                    Entrega e2 = sublista.get(j);
                    if (!e2.estaCalificada()) continue;

                    // Misma nota, actividades diferentes
                    if (e1.getNota() == e2.getNota()
                            && !e1.getNombreActividad().equals(e2.getNombreActividad())) {
                        encontrado = true;
                    }
                }
            }

            if (encontrado) {
                NodoCompuesto<Estudiante, Entrega> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(sublista);
                resultado.add(copia);
            }
        }
        return resultado;
    }

    // =========================================================
    // D) CONSULTAS SOBRE CALCULOS
    // =========================================================

    /**
     * D1 - Calculos que NO pueden ejecutarse porque algun estudiante
     *      no tiene calificacion en alguna de sus actividades.
     *
     * Un calculo "no puede ejecutarse" si hay AL MENOS UN estudiante
     * para el que falta una calificacion.
     */
    public static ListaCompuesta<Calculo, String> calculosSinPoderEjecutarse(
            ListaCompuesta<Calculo, String>    listaCalculos,
            ListaCompuesta<Estudiante, Entrega> listaEstudiantes) {

        ListaCompuesta<Calculo, String> resultado = new ListaCompuesta<>();

        for (NodoCompuesto<Calculo, String> nodoCalc = listaCalculos.getHeader();
             nodoCalc != null; nodoCalc = nodoCalc.getNext()) {

            Calculo calculo = nodoCalc.getData();
            boolean faltaAlguno = false;

            for (NodoCompuesto<Estudiante, Entrega> nodoEst = listaEstudiantes.getHeader();
                 nodoEst != null && !faltaAlguno; nodoEst = nodoEst.getNext()) {

                if (!calculo.puedeEjecutarse(nodoEst)) {
                    faltaAlguno = true;
                }
            }

            if (faltaAlguno) {
                NodoCompuesto<Calculo, String> copia = new NodoCompuesto<>(calculo);
                copia.setReferenciaLista(nodoCalc.getReferenciaLista());
                resultado.add(copia);
            }
        }
        return resultado;
    }

    /**
     * D2 - Calculos que involucran una actividad dada por nombre.
     */
    public static ListaCompuesta<Calculo, String> calculosQueInvolucran(
            ListaCompuesta<Calculo, String> listaCalculos,
            String nombreActividad) {

        ListaCompuesta<Calculo, String> resultado = new ListaCompuesta<>();
        for (NodoCompuesto<Calculo, String> nodo = listaCalculos.getHeader();
             nodo != null; nodo = nodo.getNext()) {
            if (nodo.getData().involucraActividad(nombreActividad)) {
                NodoCompuesto<Calculo, String> copia = new NodoCompuesto<>(nodo.getData());
                copia.setReferenciaLista(nodo.getReferenciaLista());
                resultado.add(copia);
            }
        }
        return resultado;
    }

    // =========================================================
    // E) ESTADISTICAS INDIVIDUALES
    // =========================================================

    /** Promedio de notas calificadas del estudiante. */
    public static double calcularPromedio(NodoCompuesto<Estudiante, Entrega> nodo) {
        ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
        if (sublista == null || sublista.isEmpty()) return 0.0;

        int suma = 0, cnt = 0;
        ListaSimple<Entrega>.Iterador it = sublista.iterador();
        while (it.hasNext()) {
            Entrega e = it.next();
            if (e.estaCalificada()) { suma += e.getNota(); cnt++; }
        }
        return cnt > 0 ? (double) suma / cnt : 0.0;
    }

    /** Nota maxima calificada del estudiante. */
    public static int notaMaxima(NodoCompuesto<Estudiante, Entrega> nodo) {
        ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
        if (sublista == null) return 0;
        int max = Integer.MIN_VALUE;
        ListaSimple<Entrega>.Iterador it = sublista.iterador();
        while (it.hasNext()) {
            Entrega e = it.next();
            if (e.estaCalificada() && e.getNota() > max) max = e.getNota();
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }

    /** Nota minima calificada del estudiante. */
    public static int notaMinima(NodoCompuesto<Estudiante, Entrega> nodo) {
        ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
        if (sublista == null) return 0;
        int min = Integer.MAX_VALUE;
        ListaSimple<Entrega>.Iterador it = sublista.iterador();
        while (it.hasNext()) {
            Entrega e = it.next();
            if (e.estaCalificada() && e.getNota() < min) min = e.getNota();
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    /** Imprime resumen estadistico de un estudiante en consola. */
    public static void imprimirEstadisticas(NodoCompuesto<Estudiante, Entrega> nodo) {
        Estudiante est = nodo.getData();
        ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
        int total = sublista != null ? sublista.getSize() : 0;
        
        // Contar entregas enviadas y calificadas
        int enviadas = 0, califc = 0;
        if (sublista != null) {
            ListaSimple<Entrega>.Iterador it = sublista.iterador();
            while (it.hasNext()) {
                Entrega e = it.next();
                if (e.fueEnviada()) enviadas++;
                if (e.estaCalificada()) califc++;
            }
        }

        System.out.println("  " + est);
        System.out.printf("    Promedio: %.2f | Max: %d | Min: %d%n",
                calcularPromedio(nodo), notaMaxima(nodo), notaMinima(nodo));
        System.out.println("    Entregas enviadas: " + enviadas + "/" + total
                + "  |  Calificadas: " + califc);
    }
}
