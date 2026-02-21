import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Programa principal del Libro de Calificaciones.
 *
 * Menu interactivo que permite:
 *   1. Ver estructura en memoria
 *   2. Ejecutar calculos (con pila y notacion postfija)
 *   3. Consultas (A1-A3, B1, C1-C3, D1-D2)
 *   4. Agregar nuevo calculo al curso
 *   5. Reporte personalizado
 *   6. Salir
 */
public class Main {

    static final String SEP  = "=".repeat(40);
    static final String SEP2 = "-".repeat(40);

    // Estructuras globales del curso
    static ListaCompuesta<Estudiante, Entrega> listaEstudiantes = new ListaCompuesta<>();
    static ListaCompuesta<Actividad,  Entrega> listaActividades  = new ListaCompuesta<>();
    static ListaCompuesta<Calculo,    String>  listaCalculos     = new ListaCompuesta<>();

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println(SEP);
        System.out.println("   Libro de calificaciones");
        System.out.println(SEP);

        // --- Carga inicial desde archivos ---
        System.out.println("\nCargando datos...\n");
        CargadorCSV.cargarNotas(resolverRutaDatos("notas.csv"),
            listaEstudiantes, listaActividades);
        System.out.println();
        CargadorCSV.cargarCalculos(resolverRutaDatos("calculos.csv"), listaCalculos);

        // --- Menu principal ---
        int opcion = -1;
        while (opcion != 6) {
            System.out.println("\n" + SEP);
            System.out.println("  Menu");
            System.out.println(SEP);
            System.out.println("  1. Ver datos");
            System.out.println("  2. Ejecutar calculos");
            System.out.println("  3. Consultas");
            System.out.println("  4. Nuevo calculo");
            System.out.println("  5. Reporte");
            System.out.println("  6. Salir");
            System.out.print("  Opcion: ");

            opcion = leerEntero();
            switch (opcion) {
                case 1: menuVerEstructura();   break;
                case 2: menuCalculos();        break;
                case 3: menuConsultas();       break;
                case 4: menuAgregarCalculo();  break;
                case 5: menuReporte();         break;
                case 6:
                    System.out.println("\n  Hasta luego.");
                    break;
                default:
                    System.out.println("  [Opcion invalida]");
            }
        }
        sc.close();
    }

    // ==============================================================
    // OPCION 1 - VER ESTRUCTURA EN MEMORIA
    // ==============================================================

    private static void menuVerEstructura() {
        System.out.println("\n" + SEP);
        System.out.println("  Datos en memoria");
        System.out.println(SEP);

        System.out.println("\n-- Estudiantes --");
        System.out.println(listaEstudiantes);

        System.out.println("\n-- Actividades --");
        System.out.println(listaActividades);

        System.out.println("\n-- Calculos --");
        if (listaCalculos.isEmpty()) {
            System.out.println("  (sin calculos)");
        } else {
            for (NodoCompuesto<Calculo, String> n = listaCalculos.getHeader();
                 n != null; n = n.getNext()) {
                Calculo c = n.getData();
                System.out.println("  " + c.getNombre());
                System.out.println("    Expresion     : " + c.getExpresionOriginal());
                System.out.println("    Postfija      : " + c.getExpresionPostfija());
                System.out.println("    Actividades   : " + c.getActividadesAsociadas());
            }
        }
    }

    // ==============================================================
    // RESOLUCION DE RUTAS DE DATOS
    // ==============================================================

    private static String resolverRutaDatos(String nombreArchivo) {
        Path directa = Paths.get("datos", nombreArchivo);
        if (Files.exists(directa)) return directa.toString();

        Path anidada = Paths.get("proyecto", "datos", nombreArchivo);
        if (Files.exists(anidada)) return anidada.toString();

        return directa.toString();
    }

    

    // ==============================================================
    // OPCION 2 - EJECUTAR CALCULOS
    // ==============================================================

    private static void menuCalculos() {
        System.out.println("\n" + SEP);
        System.out.println("  Calculos (pila / postfija)");
        System.out.println(SEP);

        if (listaCalculos.isEmpty()) {
            System.out.println("  No hay calculos definidos.");
            return;
        }

        for (NodoCompuesto<Calculo, String> nCalc = listaCalculos.getHeader();
             nCalc != null; nCalc = nCalc.getNext()) {

            Calculo calculo = nCalc.getData();
                System.out.println("\n  " + calculo.getNombre()
                    + "  [postfija: " + calculo.getExpresionPostfija() + "]");
            System.out.println("  " + SEP2);

            for (NodoCompuesto<Estudiante, Entrega> nEst = listaEstudiantes.getHeader();
                 nEst != null; nEst = nEst.getNext()) {
                try {
                    double res = calculo.evaluar(nEst);
                    System.out.printf("    %-22s -> %.2f%n", nEst.getData(), res);
                } catch (IllegalStateException e) {
                    System.out.printf("    %-22s -> Incompleto (%s)%n",
                            nEst.getData(), e.getMessage());
                }
            }
        }
    }

    // ==============================================================
    // OPCION 3 - MENU DE CONSULTAS
    // ==============================================================

    private static void menuConsultas() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n" + SEP);
            System.out.println("  Consultas");
            System.out.println(SEP);
            System.out.println("  --- Actividades ---");
            System.out.println("  1. A1 - Vencidas");
            System.out.println("  2. A2 - Incompletas");
            System.out.println("  3. A3 - Nota menor a... ");
            System.out.println("  --- Entregas ---");
            System.out.println("  4. B1 - Tardias sin calificar");
            System.out.println("  --- Estudiantes ---");
            System.out.println("  5. C1 - Porcentaje mayor a...");
            System.out.println("  6. C2 - Sin entregas vencidas");
            System.out.println("  7. C3 - Misma nota en dos actividades");
            System.out.println("  --- Calculos ---");
            System.out.println("  8. D1 - No ejecutables");
            System.out.println("  9. D2 - Involucran actividad");
            System.out.println("  --- Extra ---");
            System.out.println("  10. Filtro por fecha y nota");
            System.out.println("  0. Volver al menu principal");
            System.out.print("  Opcion: ");

            op = leerEntero();
            switch (op) {
                case 1: consultaA1(); break;
                case 2: consultaA2(); break;
                case 3: consultaA3(); break;
                case 4: consultaB1(); break;
                case 5: consultaC1(); break;
                case 6: consultaC2(); break;
                case 7: consultaC3(); break;
                case 8: consultaD1(); break;
                case 9: consultaD2(); break;
                case 10: consultaExtraComparadores(); break;
                case 0: break;
                default: System.out.println("  [Opcion invalida]");
            }
        }
    }

    // --- A1 ---
    private static void consultaA1() {
        titulo("A1 - Actividades cuya fecha limite ya fenecio");
        imprimirActividades(Consultas.actividadesVencidas(listaActividades));
    }

    // --- A2 ---
    private static void consultaA2() {
        titulo("A2 - Actividades con entregas incompletas");
        imprimirActividades(Consultas.actividadesConEntregasIncompletas(
                listaActividades, listaEstudiantes.getSize()));
    }

    // --- A3 ---
    private static void consultaA3() {
        System.out.print("  Ingrese el umbral de nota (ej. 50): ");
        int umbral = leerEntero();
        titulo("A3 - Actividades con alguna nota < " + umbral);
        imprimirActividades(Consultas.actividadesConNotaMenorA(listaActividades, umbral));
    }

    // --- B1 ---
    private static void consultaB1() {
        System.out.print("  Ingrese la fecha de referencia (yyyy-MM-dd): ");
        LocalDate fecha = leerFecha();
        if (fecha == null) return;
        titulo("B1 - Entregas enviadas despues del " + fecha + " sin calificar");
        imprimirActividadesConEntregas(
                Consultas.entregasTardiasNoCalificadas(listaActividades, fecha));
    }

    // --- C1 ---
    private static void consultaC1() {
        System.out.print("  Ingrese el porcentaje minimo (0-100, ej. 60): ");
        int pct = leerEntero();
        titulo("C1 - Estudiantes con mas del " + pct + "% de entregas realizadas");
        imprimirEstudiantes(Consultas.estudiantesConPorcentajeMayorA(
                listaEstudiantes, listaActividades.getSize(), pct));
    }

    // --- C2 ---
    private static void consultaC2() {
        titulo("C2 - Estudiantes sin entregas en actividades ya vencidas");
        imprimirEstudiantes(Consultas.estudiantesSinEntregasVencidas(
                listaEstudiantes, listaActividades));
    }

    // --- C3 ---
    private static void consultaC3() {
        titulo("C3 - Estudiantes con la misma nota en dos actividades diferentes");
        imprimirEstudiantes(Consultas.estudiantesConMismaNota(listaEstudiantes));
    }

    // --- D1 ---
    private static void consultaD1() {
        titulo("D1 - Calculos que no se pueden ejecutar (faltan calificaciones)");
        ListaCompuesta<Calculo, String> sinEjecutar =
                Consultas.calculosSinPoderEjecutarse(listaCalculos, listaEstudiantes);
        if (sinEjecutar.isEmpty()) {
            System.out.println("     (todos pueden ejecutarse para todos los estudiantes)");
        } else {
            for (NodoCompuesto<Calculo, String> n = sinEjecutar.getHeader();
                 n != null; n = n.getNext()) {
                System.out.println("     - " + n.getData().getNombre()
                        + "  [" + n.getData().getExpresionOriginal() + "]");
            }
        }
    }

    // --- D2 ---
    private static void consultaD2() {
        System.out.print("  Nombre de la actividad a buscar: ");
        String actBuscada = sc.nextLine().trim();
        titulo("D2 - Calculos que involucran '" + actBuscada + "'");
        ListaCompuesta<Calculo, String> involucran =
                Consultas.calculosQueInvolucran(listaCalculos, actBuscada);
        if (involucran.isEmpty()) {
            System.out.println("     (ningun calculo involucra esa actividad)");
        } else {
            for (NodoCompuesto<Calculo, String> n = involucran.getHeader();
                 n != null; n = n.getNext()) {
                System.out.println("     - " + n.getData().getNombre());
            }
        }
    }

    private static void consultaExtraComparadores() {
        System.out.print("  Fecha de envio antes de (yyyy-MM-dd): ");
        LocalDate fecha = leerFecha();
        if (fecha == null) return;
        System.out.print("  Nota menor a: ");
        int nota = leerEntero();

        Actividad aBuscar = new Actividad("", fecha, fecha);
        ListaCompuesta<Actividad, Entrega> porFecha =
                listaActividades.buscarTodosMayoresEnListaPrincipal(
                        new compararActividadxFecha(), aBuscar);
        ListaCompuesta<Actividad, Entrega> porNota =
                porFecha.buscarTodosMenoresEnListaSecundaria(
                        new compararEntregasxNotas(), new Entrega(nota));

        titulo("Extra - Actividades por fecha y nota");
        imprimirActividades(porNota);
    }

    // ==============================================================
    // OPCION 4 - AGREGAR NUEVO CALCULO
    // ==============================================================

    private static void menuAgregarCalculo() {
        System.out.println("\n" + SEP);
        System.out.println("  Nuevo calculo");
        System.out.println(SEP);
        System.out.println("  Formatos de expresion:");
        System.out.println("    PROMEDIO   Tarea 1|Tarea 2|Examen");
        System.out.println("    SUMA       Examen|Proyecto");
        System.out.println("    PONDERADO  Tarea 1|0.3|Examen|0.7");
        System.out.println();

        System.out.print("  Nombre del calculo: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("  [Nombre vacio, operacion cancelada]");
            return;
        }

        System.out.print("  Expresion: ");
        String expresion = sc.nextLine().trim();
        if (expresion.isEmpty()) {
            System.out.println("  [Expresion vacia, operacion cancelada]");
            return;
        }

        try {
            Calculo calculo = new Calculo(nombre, expresion);
            NodoCompuesto<Calculo, String> nodo = new NodoCompuesto<>(calculo);

            // Agregar nombres de actividades a la lista secundaria del nodo
            ListaSimple<String>.Iterador it = calculo.getActividadesAsociadas().iterador();
            while (it.hasNext()) {
                listaCalculos.addElementInSecondaryList(nodo, it.next());
            }
            listaCalculos.add(nodo);

            System.out.println("\n  Calculo agregado exitosamente:");
            System.out.println("    Nombre   : " + calculo.getNombre());
            System.out.println("    Original : " + calculo.getExpresionOriginal());
            System.out.println("    Postfija : " + calculo.getExpresionPostfija());
            System.out.println("    Usos en  : " + calculo.getActividadesAsociadas());

        } catch (IllegalArgumentException e) {
            System.out.println("  [ERROR] Expresion invalida: " + e.getMessage());
        }
    }

    // ==============================================================
    // OPCION 5 - REPORTE PERSONALIZADO
    // ==============================================================

    private static void menuReporte() {
        System.out.println("\n" + SEP);
        System.out.println("  Reporte");
        System.out.println(SEP);

        // Mostrar actividades disponibles
        System.out.println("\n  Actividades disponibles:");
        int i = 1;
        for (NodoCompuesto<Actividad, Entrega> n = listaActividades.getHeader();
             n != null; n = n.getNext()) {
            System.out.println("    " + i + ". " + n.getData().getNombre());
            i++;
        }

        System.out.println("  Ingrese los numeros de las actividades a incluir,");
        System.out.println("  separados por coma (ej: 1,3,5) o 'todas': ");
        System.out.print("  > ");
        String inputAct = sc.nextLine().trim();
        ListaSimple<String> actSeleccionadas = seleccionarDeLista(
                inputAct, listaActividades);

        // Mostrar calculos disponibles
        System.out.println("\n  Calculos disponibles:");
        int j = 1;
        for (NodoCompuesto<Calculo, String> n = listaCalculos.getHeader();
             n != null; n = n.getNext()) {
            System.out.println("    " + j + ". " + n.getData().getNombre());
            j++;
        }

        System.out.println("  Ingrese los numeros de los calculos a incluir,");
        System.out.println("  separados por coma (ej: 1,2) o 'todos' o 'ninguno': ");
        System.out.print("  > ");
        String inputCalc = sc.nextLine().trim();
        ListaSimple<String> calcSeleccionados = seleccionarCalculos(inputCalc);

        // Generar reporte
        Reporte.generar(listaEstudiantes, actSeleccionadas, listaCalculos, calcSeleccionados);
    }

    /**
     * Convierte la seleccion del usuario (numeros separados por coma o "todas")
     * en una ListaSimple de nombres de actividades.
     */
    private static ListaSimple<String> seleccionarDeLista(
            String input, ListaCompuesta<Actividad, Entrega> lista) {

        ListaSimple<String> resultado = new ListaSimple<>();

        if (input.equalsIgnoreCase("todas")) {
            for (NodoCompuesto<Actividad, Entrega> n = lista.getHeader();
                 n != null; n = n.getNext()) {
                resultado.add(n.getData().getNombre());
            }
            return resultado;
        }

        String[] partes = input.split(",");
        for (String p : partes) {
            try {
                int idx = Integer.parseInt(p.trim()) - 1;  // 1-indexado -> 0-indexado
                NodoCompuesto<Actividad, Entrega> nodo = lista.get(idx);
                if (nodo != null) {
                    resultado.add(nodo.getData().getNombre());
                } else {
                    System.out.println("  [Aviso] Numero " + (idx+1) + " fuera de rango, ignorado");
                }
            } catch (NumberFormatException e) {
                System.out.println("  [Aviso] Valor '" + p.trim() + "' ignorado");
            }
        }
        return resultado;
    }

    /**
     * Convierte la seleccion del usuario para calculos.
     */
    private static ListaSimple<String> seleccionarCalculos(String input) {
        ListaSimple<String> resultado = new ListaSimple<>();

        if (input.equalsIgnoreCase("ninguno")) return resultado;

        if (input.equalsIgnoreCase("todos")) {
            for (NodoCompuesto<Calculo, String> n = listaCalculos.getHeader();
                 n != null; n = n.getNext()) {
                resultado.add(n.getData().getNombre());
            }
            return resultado;
        }

        String[] partes = input.split(",");
        for (String p : partes) {
            try {
                int idx = Integer.parseInt(p.trim()) - 1;
                NodoCompuesto<Calculo, String> nodo = listaCalculos.get(idx);
                if (nodo != null) {
                    resultado.add(nodo.getData().getNombre());
                } else {
                    System.out.println("  [Aviso] Numero " + (idx+1) + " fuera de rango, ignorado");
                }
            } catch (NumberFormatException e) {
                System.out.println("  [Aviso] Valor '" + p.trim() + "' ignorado");
            }
        }
        return resultado;
    }

    // ==============================================================
    // AUXILIARES
    // ==============================================================

    private static void titulo(String texto) {
        System.out.println("\n  >> " + texto + ":");
    }

    private static void imprimirActividades(ListaCompuesta<Actividad, Entrega> lista) {
        if (lista.isEmpty()) { System.out.println("     (ninguna)"); return; }
        for (NodoCompuesto<Actividad, Entrega> n = lista.getHeader();
             n != null; n = n.getNext()) {
            System.out.println("     - " + n.getData());
        }
    }

    private static void imprimirActividadesConEntregas(
            ListaCompuesta<Actividad, Entrega> lista) {
        if (lista.isEmpty()) { System.out.println("     (ninguna)"); return; }
        for (NodoCompuesto<Actividad, Entrega> n = lista.getHeader();
             n != null; n = n.getNext()) {
            System.out.println("     Actividad: " + n.getData().getNombre());
            if (n.getReferenciaLista() != null) {
                ListaSimple<Entrega>.Iterador it = n.getReferenciaLista().iterador();
                while (it.hasNext()) System.out.println("       * " + it.next());
            }
        }
    }

    private static void imprimirEstudiantes(ListaCompuesta<Estudiante, Entrega> lista) {
        if (lista.isEmpty()) { System.out.println("     (ninguno)"); return; }
        for (NodoCompuesto<Estudiante, Entrega> n = lista.getHeader();
             n != null; n = n.getNext()) {
            System.out.println("     - " + n.getData());
        }
    }

    /** Lee un entero desde consola; retorna -99 si la entrada no es valida. */
    private static int leerEntero() {
        try {
            String linea = sc.nextLine().trim();
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            return -99;
        }
    }

    /** Lee una fecha en formato yyyy-MM-dd desde consola; retorna null si es invalida. */
    private static LocalDate leerFecha() {
        try {
            String linea = sc.nextLine().trim();
            return LocalDate.parse(linea);
        } catch (DateTimeParseException e) {
            System.out.println("  [Fecha invalida, use formato yyyy-MM-dd]");
            return null;
        }
    }
}
