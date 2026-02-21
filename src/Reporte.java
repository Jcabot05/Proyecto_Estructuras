public class Reporte {

    public static void generar(
            ListaCompuesta<Estudiante, Entrega> listaEstudiantes,
            ListaSimple<String> actividades,
            ListaCompuesta<Calculo, String> listaCalculos,
            ListaSimple<String> calculosIncluir) {

        System.out.println(construir(listaEstudiantes, actividades, listaCalculos, calculosIncluir));
    }

    private static String construir(
            ListaCompuesta<Estudiante, Entrega> listaEstudiantes,
            ListaSimple<String> actividades,
            ListaCompuesta<Calculo, String> listaCalculos,
            ListaSimple<String> calculosIncluir) {

        int ancho1 = 20;
        int ancho2 = 12;
        int cols = actividades.getSize() + calculosIncluir.getSize();
        int total = ancho1 + cols * ancho2 + 2;
        String linea = "";
        for (int i = 0; i < total; i++) linea += "-";

        String resultado = "";
        resultado += "\nREPORTE DE CALIFICACIONES\n";
        resultado += linea + "\n";

        resultado += String.format("%-" + ancho1 + "s", "ESTUDIANTE");
        ListaSimple<String>.Iterador itA = actividades.iterador();
        while (itA.hasNext()) {
            String nombre = itA.next();
            if (nombre.length() > ancho2 - 2) nombre = nombre.substring(0, ancho2 - 4) + "..";
            resultado += String.format("%-" + ancho2 + "s", nombre);
        }
        ListaSimple<String>.Iterador itC = calculosIncluir.iterador();
        while (itC.hasNext()) {
            String nombre = itC.next();
            if (nombre.length() > ancho2 - 2) nombre = nombre.substring(0, ancho2 - 4) + "..";
            resultado += String.format("%-" + ancho2 + "s", nombre);
        }
        resultado += "\n" + linea + "\n";

        for (NodoCompuesto<Estudiante, Entrega> nodoEst = listaEstudiantes.getHeader();
             nodoEst != null; nodoEst = nodoEst.getNext()) {

            String est = nodoEst.getData().toString();
            if (est.length() > ancho1 - 2) est = est.substring(0, ancho1 - 4) + "..";
            resultado += String.format("%-" + ancho1 + "s", est);

            itA = actividades.iterador();
            while (itA.hasNext()) {
                String celda = notaEstudiante(nodoEst, itA.next());
                resultado += String.format("%-" + ancho2 + "s", celda);
            }

            itC = calculosIncluir.iterador();
            while (itC.hasNext()) {
                String celda = resultadoCalculo(listaCalculos, nodoEst, itC.next());
                resultado += String.format("%-" + ancho2 + "s", celda);
            }

            resultado += "\n";
        }

        resultado += linea + "\n";
        resultado += "  Actividades : " + actividades + "\n";
        resultado += "  Calculos    : " + calculosIncluir + "\n";
        resultado += "  Leyenda     : N/A=no entregado  S/C=sin calificar\n";
        resultado += linea + "\n";

        return resultado;
    }

    private static String notaEstudiante(NodoCompuesto<Estudiante, Entrega> nodo, String nombreActividad) {
        ListaSimple<Entrega> entregas = nodo.getReferenciaLista();
        if (entregas == null) return "N/A";

        ListaSimple<Entrega>.Iterador it = entregas.iterador();
        while (it.hasNext()) {
            Entrega e = it.next();
            if (e.getNombreActividad().equals(nombreActividad)) {
                if (!e.fueEnviada()) return "N/A";
                if (!e.estaCalificada()) return "S/C";
                return String.valueOf(e.getNota());
            }
        }
        return "N/A";
    }

    private static String resultadoCalculo(
            ListaCompuesta<Calculo, String> listaCalculos,
            NodoCompuesto<Estudiante, Entrega> nodoEst,
            String nombreCalculo) {

        for (NodoCompuesto<Calculo, String> nodo = listaCalculos.getHeader();
             nodo != null; nodo = nodo.getNext()) {

            if (nodo.getData().getNombre().equals(nombreCalculo)) {
                try {
                    return String.format("%.2f", nodo.getData().evaluar(nodoEst));
                } catch (Exception e) {
                    return "Incompleto";
                }
            }
        }
        return "N/A";
    }
}
