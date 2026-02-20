import java.util.Stack;

/**
 * Representa un calculo agregado del curso.
 *
 * =================================================================
 * NOTACION SIMPLE (lo que escribe el usuario en el CSV):
 * =================================================================
 *
 *   PROMEDIO  act1|act2|act3
 *     -> promedio aritmetico de las actividades listadas
 *     Ejemplo:  "PROMEDIO Tarea 1|Tarea 2"
 *
 *   SUMA  act1|act2|act3
 *     -> suma todas las notas listadas
 *     Ejemplo:  "SUMA Examen|Taller 1"
 *
 *   PONDERADO  act1|peso1|act2|peso2
 *     -> suma ponderada: nota1*peso1 + nota2*peso2 + ...
 *     Los pesos son numeros decimales (0.4 = 40%)
 *     Ejemplo:  "PONDERADO Tarea 1|0.3|Examen|0.7"
 *
 * Los nombres de actividades se separan con "|" para permitir
 * espacios dentro del nombre (ej. "Tarea 1").
 *
 * =================================================================
 * MECANISMO INTERNO (obligatorio por el enunciado):
 * =================================================================
 *
 * La notacion simple se convierte automaticamente a notacion POSTFIJA
 * al construir el objeto. La evaluacion usa java.util.Stack.
 *
 * Ejemplo:
 *   "PROMEDIO Tarea 1|Tarea 2"
 *   tokens postfijos: ["Tarea 1", "Tarea 2", "+", "2.0", "/"]
 *
 *   Ejecucion con pila:
 *     push("Tarea 1" -> nota)   pila: [30.0]
 *     push("Tarea 2" -> nota)   pila: [30.0, 85.0]
 *     "+"  -> pop 85, pop 30, push 115   pila: [115.0]
 *     push(2.0)                 pila: [115.0, 2.0]
 *     "/"  -> pop 2, pop 115, push 57.5  pila: [57.5]
 *     resultado = 57.5
 */
public class Calculo {

    private String              nombre;
    private String              expresionOriginal;   // texto del CSV, para mostrar al usuario
    private String[]            tokensPostfijos;     // expresion convertida, para evaluar con pila
    private ListaSimple<String> actividadesAsociadas;

    // =========================================================
    // Constructor
    // =========================================================

    public Calculo(String nombre, String expresionSimple) {
        this.nombre              = nombre;
        this.expresionOriginal   = expresionSimple.trim();
        this.actividadesAsociadas = new ListaSimple<>();
        this.tokensPostfijos     = convertirAPostfija(this.expresionOriginal);

        // Registrar los nombres de actividades encontrados en la expresion
        for (String token : tokensPostfijos) {
            if (!esOperador(token) && !esNumero(token)) {
                if (!actividadesAsociadas.contiene(token)) {
                    actividadesAsociadas.add(token);
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
            case "PROMEDIO": return postfijaLista(tokenizar(resto), true);
            case "SUMA":     return postfijaLista(tokenizar(resto), false);
            case "PONDERADO":return postfijaPonderado(tokenizar(resto));
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
    private String[] tokenizar(String texto) {
        String[] partes = texto.split("\\|");
        for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
        return partes;
    }

    /**
     * Genera tokens postfijos para SUMA o PROMEDIO de una lista de actividades.
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
        String[] tokens = new String[nombres.length + (nombres.length - 1) + extra];
        int i = 0;

        tokens[i++] = nombres[0];
        for (int k = 1; k < nombres.length; k++) {
            tokens[i++] = nombres[k];
            tokens[i++] = "+";
        }
        if (esPromedio) {
            tokens[i++] = String.valueOf((double) nombres.length);
            tokens[i++] = "/";
        }
        return tokens;
    }

    /**
     * Genera tokens postfijos para PONDERADO.
     *
     * Entrada (partes alternadas): [act1, peso1, act2, peso2, ...]
     * Salida postfija para dos terminos: act1 peso1 * act2 peso2 * +
     */
    private String[] postfijaPonderado(String[] partes) {
        if (partes.length % 2 != 0)
            throw new IllegalArgumentException(
                "PONDERADO requiere pares (actividad|peso). Tokens: " + partes.length);

        int pares = partes.length / 2;
        // Por par: 3 tokens (act, peso, *); entre pares: 1 "+"
        String[] tokens = new String[pares * 3 + (pares - 1)];
        int i = 0;

        for (int k = 0; k < partes.length; k += 2) {
            String act  = partes[k];
            String peso = partes[k + 1];
            if (!esNumero(peso))
                throw new IllegalArgumentException(
                    "Peso invalido en PONDERADO: '" + peso + "'");
            tokens[i++] = act;
            tokens[i++] = peso;
            tokens[i++] = "*";
            if (k + 2 < partes.length) tokens[i++] = "+";
        }
        return tokens;
    }

    // =========================================================
    // Evaluacion con Pila (java.util.Stack)
    // =========================================================

    /**
     * Evalua la expresion postfija para el estudiante dado.
     *
     * Algoritmo (evaluacion de expresiones postfijas con pila):
     *   Para cada token:
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

        for (String token : tokensPostfijos) {

            if (esNumero(token)) {
                pila.push(Double.parseDouble(token));

            } else if (esOperador(token)) {
                if (pila.size() < 2)
                    throw new IllegalStateException(
                        "Expresion mal formada en '" + nombre + "'");
                double b = pila.pop();
                double a = pila.pop();
                pila.push(operar(a, b, token));

            } else {
                // Nombre de actividad
                double nota = buscarNota(nodoEst, token);
                if (nota < 0)
                    throw new IllegalStateException(
                        "Falta calificacion de '" + token
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
        for (int i = 0; i < tokensPostfijos.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(tokensPostfijos[i]);
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
