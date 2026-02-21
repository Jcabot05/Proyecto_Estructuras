import java.util.Stack;

/**
 * Calculo agregado del curso (suma, promedio o ponderado).
 * La expresion simple se convierte a postfija y se evalua con pila.
 */
public class Calculo {

    private String              nombre;
    private String              expresionOriginal;   // texto del CSV, para mostrar al usuario
    private String[]            pasosPostfijos;      // expresion convertida, para evaluar con pila
    private ListaSimple<String> actividadesAsociadas;

    // =========================================================
    // Constructor
    // =========================================================

    public Calculo(String nombre, String expresionSimple) {
        this.nombre              = nombre;
        this.expresionOriginal   = expresionSimple.trim();
        this.actividadesAsociadas = new ListaSimple<>();
        this.pasosPostfijos      = convertirAPostfija(this.expresionOriginal);

        // Registrar los nombres de actividades encontrados en la expresion
        for (String paso : pasosPostfijos) {
            if (!esOperador(paso) && !esNumero(paso)) {
                if (!actividadesAsociadas.contiene(paso)) {
                    actividadesAsociadas.add(paso);
                }
            }
        }
    }

    // =========================================================
    // Conversion notacion simple -> postfija
    // =========================================================

    private String[] convertirAPostfija(String expresion) {
        int primer = expresion.indexOf(' ');
        if (primer < 0) return new String[]{expresion};

        String clave = expresion.substring(0, primer).toUpperCase();
        String resto = expresion.substring(primer + 1).trim();

        switch (clave) {
            case "PROMEDIO": return postfijaLista(separarPartes(resto), true);
            case "SUMA":     return postfijaLista(separarPartes(resto), false);
            case "PONDERADO":return postfijaPonderado(separarPartes(resto));
            default:
                throw new IllegalArgumentException(
                    "Operacion desconocida: '" + clave
                    + "'. Use PROMEDIO, SUMA o PONDERADO.");
        }
    }

    /**
     * Divide la parte de actividades separadas por "|".
     * "Tarea 1|Tarea 2|Examen" -> ["Tarea 1", "Tarea 2", "Examen"]
     */
    private String[] separarPartes(String texto) {
        String[] partes = texto.split("\\|");
        for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
        return partes;
    }

    /**
    * Genera pasos postfijos para SUMA o PROMEDIO de una lista de actividades.
     *
     * SUMA    [A, B, C]  =>  A B + C +
     * PROMEDIO [A, B, C] =>  A B + C + 3.0 /
     */
    private String[] postfijaLista(String[] nombres, boolean esPromedio) {
        if (nombres.length == 0)
            throw new IllegalArgumentException("La expresion no contiene actividades.");
        if (nombres.length == 1) {
            return esPromedio ? new String[]{nombres[0]}
                             : new String[]{nombres[0]};
        }

        // Suma encadenada: n nombres + (n-1) operadores "+"
        int extra = esPromedio ? 2 : 0;  // + literal_n + "/"
        String[] pasos = new String[nombres.length + (nombres.length - 1) + extra];
        int i = 0;

        pasos[i++] = nombres[0];
        for (int k = 1; k < nombres.length; k++) {
            pasos[i++] = nombres[k];
            pasos[i++] = "+";
        }
        if (esPromedio) {
            pasos[i++] = String.valueOf((double) nombres.length);
            pasos[i++] = "/";
        }
        return pasos;
    }

    /**
    * Genera pasos postfijos para PONDERADO.
     *
     * Entrada (partes alternadas): [act1, peso1, act2, peso2, ...]
     * Salida postfija para dos terminos: act1 peso1 * act2 peso2 * +
     */
    private String[] postfijaPonderado(String[] partes) {
        if (partes.length % 2 != 0)
            throw new IllegalArgumentException(
                "PONDERADO requiere pares (actividad|peso). Partes: " + partes.length);

        int pares = partes.length / 2;
        // Por par: 3 pasos (act, peso, *); entre pares: 1 "+"
        String[] pasos = new String[pares * 3 + (pares - 1)];
        int i = 0;

        // Termino inicial: act1 peso1 *
        String actInicial = partes[0];
        String pesoInicial = partes[1];
        if (!esNumero(pesoInicial))
            throw new IllegalArgumentException(
                "Peso invalido en PONDERADO: '" + pesoInicial + "'");
        pasos[i++] = actInicial;
        pasos[i++] = pesoInicial;
        pasos[i++] = "*";

        // Siguientes terminos: actN pesoN * +
        for (int k = 2; k < partes.length; k += 2) {
            String act  = partes[k];
            String peso = partes[k + 1];
            if (!esNumero(peso))
                throw new IllegalArgumentException(
                    "Peso invalido en PONDERADO: '" + peso + "'");
            pasos[i++] = act;
            pasos[i++] = peso;
            pasos[i++] = "*";
            pasos[i++] = "+";
        }
        return pasos;
    }

    // =========================================================
    // Evaluacion con Pila (java.util.Stack)
    // =========================================================

    /**
     * Evalua la expresion postfija para el estudiante dado.
     *
     * Algoritmo (evaluacion de expresiones postfijas con pila):
    *   Para cada paso:
     *     numero     -> pila.push(numero)
     *     operador   -> b=pila.pop(), a=pila.pop(), pila.push(a op b)
     *     actividad  -> buscar nota, pila.push(nota)
     *   Resultado = pila.pop()
     *
     * @throws IllegalStateException si falta alguna calificacion
     */
    public double evaluar(NodoCompuesto<Estudiante, Entrega> nodoEst)
            throws IllegalStateException {

        Stack<Double> pila = new Stack<>();

        for (String paso : pasosPostfijos) {

            if (esNumero(paso)) {
                pila.push(Double.parseDouble(paso));

            } else if (esOperador(paso)) {
                if (pila.size() < 2)
                    throw new IllegalStateException(
                        "Expresion mal formada en '" + nombre + "'");
                double b = pila.pop();
                double a = pila.pop();
                pila.push(operar(a, b, paso));

            } else {
                // Nombre de actividad
                double nota = buscarNota(nodoEst, paso);
                if (nota < 0)
                    throw new IllegalStateException(
                        "Falta calificacion de '" + paso
                        + "' para " + nodoEst.getData());
                pila.push(nota);
            }
        }

        if (pila.isEmpty())
            throw new IllegalStateException("Expresion vacia: " + nombre);
        return pila.pop();
    }

    /** Indica si el calculo puede ejecutarse (todas las notas disponibles). */
    public boolean puedeEjecutarse(NodoCompuesto<Estudiante, Entrega> nodoEst) {
        ListaSimple<Entrega> entregas = nodoEst.getReferenciaLista();
        if (entregas == null) return false;
        ListaSimple<String>.Iterador it = actividadesAsociadas.iterador();
        while (it.hasNext()) {
            String act = it.next();
            boolean ok = false;
            ListaSimple<Entrega>.Iterador itE = entregas.iterador();
            while (itE.hasNext()) {
                Entrega e = itE.next();
                if (e.getNombreActividad().equals(act) && e.estaCalificada()) { ok = true; break; }
            }
            if (!ok) return false;
        }
        return true;
    }

    /** Indica si este calculo involucra la actividad dada. */
    public boolean involucraActividad(String nombreActividad) {
        return actividadesAsociadas.contiene(nombreActividad);
    }

    // =========================================================
    // Auxiliares privados
    // =========================================================

    private boolean esOperador(String t) {
        return "+".equals(t) || "-".equals(t) || "*".equals(t) || "/".equals(t);
    }

    private boolean esNumero(String t) {
        if (t == null || t.isEmpty()) return false;
        try { Double.parseDouble(t); return true; }
        catch (NumberFormatException e) { return false; }
    }

    private double operar(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new IllegalStateException("Division por cero en '" + nombre + "'");
                return a / b;
            default: throw new IllegalStateException("Operador desconocido: " + op);
        }
    }

    private double buscarNota(NodoCompuesto<Estudiante, Entrega> nodo, String act) {
        ListaSimple<Entrega> entregas = nodo.getReferenciaLista();
        if (entregas == null) return -1.0;
        ListaSimple<Entrega>.Iterador it = entregas.iterador();
        while (it.hasNext()) {
            Entrega e = it.next();
            if (e.getNombreActividad().equals(act) && e.estaCalificada()) return e.getNota();
        }
        return -1.0;
    }

    // =========================================================
    // Getters
    // =========================================================

    public String getNombre()            { return nombre; }
    public String getExpresionOriginal() { return expresionOriginal; }
    public ListaSimple<String> getActividadesAsociadas() { return actividadesAsociadas; }

    /** Retorna la expresion postfija interna (para depuracion o documentacion). */
    public String getExpresionPostfija() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pasosPostfijos.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(pasosPostfijos[i]);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Calculo[" + nombre + " | " + expresionOriginal + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return nombre.equals(((Calculo) o).nombre);
    }

    @Override
    public int hashCode() { return nombre.hashCode(); }
}
