import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga datos desde CSV.
 */
public class CargadorCSV {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // =========================================================
    // Carga principal de notas
    // =========================================================

    /**
     * Carga estudiantes, actividades y entregas desde el archivo CSV indicado.
     * Las filas con nota vacia se registran como entregas sin calificar.
     * Las filas con nota vacia Y sin fechaEntrega se registran como no enviadas.
     */
    public static void cargarNotas(String rutaArchivo,
                                   ListaCompuesta<Estudiante, Entrega> listaEstudiantes,
                                   ListaCompuesta<Actividad, Entrega> listaActividades) {

        Map<String, NodoCompuesto<Estudiante, Entrega>> mapaEstudiantes = new HashMap<>();
        Map<String, NodoCompuesto<Actividad, Entrega>>  mapaActividades  = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;
            int fila = 0;
            int errores = 0;

            // Saltar encabezado
            br.readLine();

            while ((linea = br.readLine()) != null) {
                fila++;
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(",", -1); // -1 preserva celdas vacias

                if (datos.length < 4) {
                    System.out.println("  [Aviso] Fila " + fila + " ignorada: pocas columnas");
                    errores++;
                    continue;
                }

                String nombreEstudiante = datos[0].trim();
                String notaStr          = datos[1].trim();
                String nombreActividad  = datos[2].trim();
                String fechaLimiteStr   = datos[3].trim();
                String fechaEntregaStr  = datos.length >= 5 ? datos[4].trim() : "";

                // --- Fecha limite de la actividad ---
                LocalDate fechaLimite;
                try {
                    fechaLimite = LocalDate.parse(fechaLimiteStr, FMT);
                } catch (Exception e) {
                        System.out.println("  [Aviso] Fila " + fila
                            + ": fecha limite invalida '" + fechaLimiteStr + "'");
                    fechaLimite = LocalDate.now().plusDays(30);
                    errores++;
                }

                // --- Fecha de entrega del estudiante (puede estar vacia) ---
                LocalDate fechaEntrega = null;
                if (!fechaEntregaStr.isEmpty()) {
                    try {
                        fechaEntrega = LocalDate.parse(fechaEntregaStr, FMT);
                    } catch (Exception e) {
                        System.out.println("  [Aviso] Fila " + fila
                            + ": fecha entrega invalida '" + fechaEntregaStr + "'");
                    }
                }

                // --- Nota (puede estar vacia = sin calificar) ---
                boolean tieneNota = !notaStr.isEmpty();
                int nota = -1;
                if (tieneNota) {
                    try {
                        nota = Integer.parseInt(notaStr);
                    } catch (NumberFormatException e) {
                        System.out.println("  [Aviso] Fila " + fila
                            + ": nota invalida '" + notaStr + "' -> sin calificar");
                        tieneNota = false;
                        errores++;
                    }
                }

                // ============================================================
                // 1. Nodo de ACTIVIDAD
                // ============================================================
                NodoCompuesto<Actividad, Entrega> nodoAct = mapaActividades.get(nombreActividad);
                if (nodoAct == null) {
                    Actividad actividad = new Actividad(nombreActividad, fechaLimite);
                    nodoAct = new NodoCompuesto<>(actividad);
                    listaActividades.add(nodoAct);
                    mapaActividades.put(nombreActividad, nodoAct);
                }

                // ============================================================
                // 2. Nodo de ESTUDIANTE
                // ============================================================
                NodoCompuesto<Estudiante, Entrega> nodoEst = mapaEstudiantes.get(nombreEstudiante);
                if (nodoEst == null) {
                    String[] partes = nombreEstudiante.split(" ", 2);
                    String nombre   = partes[0];
                    String apellido = partes.length > 1 ? partes[1] : "";
                    Estudiante est  = new Estudiante(nombre, apellido);
                    nodoEst = new NodoCompuesto<>(est);
                    listaEstudiantes.add(nodoEst);
                    mapaEstudiantes.put(nombreEstudiante, nodoEst);
                }

                // ============================================================
                // 3. Crear entrega y agregarla a ambas listas
                // ============================================================
                Entrega entregaParaEstudiante;
                Entrega entregaParaActividad;

                if (tieneNota) {
                    entregaParaEstudiante = new Entrega(nombreActividad, nombreEstudiante, fechaEntrega, nota);
                    entregaParaActividad  = new Entrega(nombreActividad, nombreEstudiante, fechaEntrega, nota);
                } else {
                    // Sin nota: registramos igual (puede haberse enviado o no)
                    entregaParaEstudiante = new Entrega(nombreActividad, nombreEstudiante, fechaEntrega);
                    entregaParaActividad  = new Entrega(nombreActividad, nombreEstudiante, fechaEntrega);
                }

                listaEstudiantes.addElementInSecondaryList(nodoEst, entregaParaEstudiante);
                listaActividades.addElementInSecondaryList(nodoAct, entregaParaActividad);
            }

            // Actualizar el total de estudiantes en cada actividad
            int totalEstudiantes = listaEstudiantes.getSize();
            for (NodoCompuesto<Actividad, Entrega> nodo = listaActividades.getHeader();
                 nodo != null; nodo = nodo.getNext()) {
                nodo.getData().setTotalEstudiantesCurso(totalEstudiantes);
            }

            System.out.println("=".repeat(55));
            System.out.println("CARGA DE NOTAS");
            System.out.println("  Estudiantes: " + listaEstudiantes.getSize());
            System.out.println("  Actividades: " + listaActividades.getSize());
            System.out.println("  Filas: " + fila + "  (errores: " + errores + ")");
            System.out.println("=".repeat(55));

        } catch (IOException e) {
            System.err.println("[Error] No se pudo leer el archivo: " + e.getMessage());
        }
    }

    // =========================================================
    // Carga de calculos agregados
    // =========================================================

    /**
     * Carga calculos desde CSV.
     */
    public static void cargarCalculos(String rutaArchivo,
                                      ListaCompuesta<Calculo, String> listaCalculos) {

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;
            int fila = 0;

            // Saltar encabezado
            br.readLine();

            while ((linea = br.readLine()) != null) {
                fila++;
                if (linea.trim().isEmpty()) continue;

                // Columnas separadas por ";;" para evitar conflicto con "|" de las actividades.
                // Ejemplo de linea: "Promedio Tareas;;PROMEDIO Tarea 1|Tarea 2"
                int primerSep = linea.indexOf(";;");
                if (primerSep < 0) {
                    System.out.println("  [Aviso] Fila " + fila + " de calculos ignorada (sin ';;')");
                    continue;
                }

                String nombreCalculo = linea.substring(0, primerSep).trim();
                String expresion     = linea.substring(primerSep + 2).trim();

                try {
                    Calculo calculo = new Calculo(nombreCalculo, expresion);
                    NodoCompuesto<Calculo, String> nodo = new NodoCompuesto<>(calculo);

                    // Agregar nombres de actividades a la lista secundaria
                    ListaSimple<String>.Iterador it =
                            calculo.getActividadesAsociadas().iterador();
                    while (it.hasNext()) {
                        listaCalculos.addElementInSecondaryList(nodo, it.next());
                    }

                    listaCalculos.add(nodo);
                        System.out.println("  + Calculo: " + calculo.getNombre()
                            + " [postfija: " + calculo.getExpresionPostfija() + "]");

                } catch (IllegalArgumentException e) {
                        System.out.println("  [Error] Calculo en fila " + fila
                            + " invalido: " + e.getMessage());
                }
            }

            System.out.println("Calculos cargados: " + listaCalculos.getSize());

        } catch (IOException e) {
            System.err.println("[Error] No se pudo leer calculos: " + e.getMessage());
        }
    }
}
