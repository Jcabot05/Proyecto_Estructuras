/**
 * Reporte simple en consola.
 */
public class Reporte {

    /**
     * Genera e imprime el reporte en consola.
     *
     * @param listaEstudiantes  todos los estudiantes del curso
     * @param actividades       subconjunto de nombres de actividades a incluir
     * @param listaCalculos     lista completa de calculos del sistema
     * @param calculosIncluir   subconjunto de nombres de calculos a incluir
     */
    public static void generar(
            ListaCompuesta<Estudiante, Entrega>  listaEstudiantes,
            ListaSimple<String>                  actividades,
            ListaCompuesta<Calculo, String>      listaCalculos,
            ListaSimple<String>                  calculosIncluir) {

        System.out.println(construir(listaEstudiantes, actividades,
                                     listaCalculos, calculosIncluir));
    }

    // =========================================================
    // Construccion del reporte
    // =========================================================

    private static String construir(
            ListaCompuesta<Estudiante, Entrega>  listaEstudiantes,
            ListaSimple<String>                  actividades,
            ListaCompuesta<Calculo, String>      listaCalculos,
            ListaSimple<String>                  calculosIncluir) {

        final int COL_EST  = 20;
        final int COL_DATO = 12;

        int cols = actividades.getSize() + calculosIncluir.getSize();
        int anchoTotal = COL_EST + cols * COL_DATO + 2;

        String lineaSimple = "-".repeat(anchoTotal);

        StringBuilder sb = new StringBuilder();

        // ---------- Titulo ----------
        sb.append("\nREPORTE DE CALIFICACIONES\n");
        sb.append(lineaSimple).append("\n");

        // ---------- Encabezado de columnas ----------
        sb.append(String.format("%-" + COL_EST + "s", "ESTUDIANTE"));

        ListaSimple<String>.Iterador itA = actividades.iterador();
        while (itA.hasNext()) {
            sb.append(String.format("%-" + COL_DATO + "s", truncar(itA.next(), COL_DATO - 2)));
        }
        ListaSimple<String>.Iterador itC = calculosIncluir.iterador();
        while (itC.hasNext()) {
            String cNombre = truncar(itC.next(), COL_DATO - 2);
            sb.append(String.format("%-" + COL_DATO + "s", cNombre));
        }
        sb.append("\n").append(lineaSimple).append("\n");

        // ---------- Fila por estudiante ----------
        for (NodoCompuesto<Estudiante, Entrega> nodoEst = listaEstudiantes.getHeader();
             nodoEst != null; nodoEst = nodoEst.getNext()) {

            sb.append(String.format("%-" + COL_EST + "s",
                    truncar(nodoEst.getData().toString(), COL_EST - 2)));

            // Notas de actividades seleccionadas
            itA = actividades.iterador();
            while (itA.hasNext()) {
                String celda = notaEstudiante(nodoEst, itA.next());
                sb.append(String.format("%-" + COL_DATO + "s", celda));
            }

            // Resultados de calculos seleccionados
            itC = calculosIncluir.iterador();
            while (itC.hasNext()) {
                String celda = resultadoCalculo(listaCalculos, nodoEst, itC.next());
                sb.append(String.format("%-" + COL_DATO + "s", celda));
            }

            sb.append("\n");
        }

        // ---------- Pie ----------
        sb.append(lineaSimple).append("\n");
        sb.append("  Actividades : ").append(actividades).append("\n");
        sb.append("  Calculos    : ").append(calculosIncluir).append("\n");
        sb.append("  Leyenda     : N/A=no entregado  S/C=sin calificar\n");
        sb.append(lineaSimple).append("\n");

        return sb.toString();
    }

    // =========================================================
    // Auxiliares privados
    // =========================================================

    /** Obtiene la nota de un estudiante para una actividad. */
    private static String notaEstudiante(
            NodoCompuesto<Estudiante, Entrega> nodo, String nombreActividad) {

        ListaSimple<Entrega> sublista = nodo.getReferenciaLista();
        if (sublista == null) return "N/A";

        ListaSimple<Entrega>.Iterador it = sublista.iterador();
        while (it.hasNext()) {
            Entrega e = it.next();
            if (e.getNombreActividad().equals(nombreActividad)) {
                if (!e.fueEnviada())     return "N/A";
                if (!e.estaCalificada()) return "S/C";
                return String.valueOf(e.getNota());
            }
        }
        return "N/A";
    }

    /** Evalua un calculo para un estudiante. */
    private static String resultadoCalculo(
            ListaCompuesta<Calculo, String>    listaCalculos,
            NodoCompuesto<Estudiante, Entrega> nodoEst,
            String nombreCalculo) {

        for (NodoCompuesto<Calculo, String> nodo = listaCalculos.getHeader();
             nodo != null; nodo = nodo.getNext()) {

            if (nodo.getData().getNombre().equals(nombreCalculo)) {
                try {
                    return String.format("%.2f", nodo.getData().evaluar(nodoEst));
                } catch (IllegalStateException e) {
                    return "Incompleto";
                }
            }
        }
        return "N/A";
    }

    private static String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() <= max ? texto : texto.substring(0, max - 2) + "..";
    }

    
}
