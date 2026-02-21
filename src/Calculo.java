import java.util.Stack;

public class Calculo {

    private String nombre;
    private String expresion;
    private String[] pasos;
    private ListaSimple<String> actividades;

    public Calculo(String nombre, String expresionSimple) {
        this.nombre = nombre;
        this.expresion = expresionSimple.trim();
        this.actividades = new ListaSimple<>();
        this.pasos = convertir(this.expresion);
        for (String paso : pasos) {
            if (!esOperador(paso) && !esNumero(paso)) {
                if (!actividades.contiene(paso)) {
                    actividades.add(paso);
                }
            }
        }
    }

    private String[] convertir(String expr) {
        int pos = expr.indexOf(' ');
        if (pos < 0) return new String[]{expr};

        String tipo = expr.substring(0, pos).toUpperCase();
        String resto = expr.substring(pos + 1).trim();
        String[] partes = resto.split("\\|");
        for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();

        if (tipo.equals("PROMEDIO")) {
            return hacerPromedio(partes);
        } else if (tipo.equals("SUMA")) {
            return hacerSuma(partes);
        } else if (tipo.equals("PONDERADO")) {
            return hacerPonderado(partes);
        }
        return new String[]{expr};
    }

    private String[] hacerPromedio(String[] nombres) {
        if (nombres.length == 1) return new String[]{nombres[0]};
        String[] resultado = new String[nombres.length * 2];
        int i = 0;
        resultado[i++] = nombres[0];
        for (int k = 1; k < nombres.length; k++) {
            resultado[i++] = nombres[k];
            resultado[i++] = "+";
        }
        resultado[i++] = String.valueOf((double) nombres.length);
        resultado[i++] = "/";
        return resultado;
    }

    private String[] hacerSuma(String[] nombres) {
        if (nombres.length == 1) return new String[]{nombres[0]};
        String[] resultado = new String[nombres.length * 2 - 1];
        int i = 0;
        resultado[i++] = nombres[0];
        for (int k = 1; k < nombres.length; k++) {
            resultado[i++] = nombres[k];
            resultado[i++] = "+";
        }
        return resultado;
    }

    private String[] hacerPonderado(String[] partes) {
        int pares = partes.length / 2;
        String[] resultado = new String[pares * 4 - 1];
        int i = 0;
        resultado[i++] = partes[0];
        resultado[i++] = partes[1];
        resultado[i++] = "*";
        for (int k = 2; k < partes.length; k += 2) {
            resultado[i++] = partes[k];
            resultado[i++] = partes[k + 1];
            resultado[i++] = "*";
            resultado[i++] = "+";
        }
        return resultado;
    }

    public double evaluar(NodoCompuesto<Estudiante, Entrega> nodoEst) {
        Stack<Double> pila = new Stack<>();
        for (String paso : pasos) {
            if (esNumero(paso)) {
                pila.push(Double.parseDouble(paso));
            } else if (esOperador(paso)) {
                double b = pila.pop();
                double a = pila.pop();
                if (paso.equals("+")) pila.push(a + b);
                else if (paso.equals("-")) pila.push(a - b);
                else if (paso.equals("*")) pila.push(a * b);
                else if (paso.equals("/")) pila.push(a / b);
            } else {
                double nota = buscarNota(nodoEst, paso);
                if (nota < 0) throw new IllegalStateException("Falta nota de " + paso);
                pila.push(nota);
            }
        }
        return pila.pop();
    }

    public boolean puedeEjecutarse(NodoCompuesto<Estudiante, Entrega> nodoEst) {
        ListaSimple<Entrega> entregas = nodoEst.getReferenciaLista();
        if (entregas == null) return false;
        ListaSimple<String>.Iterador it = actividades.iterador();
        while (it.hasNext()) {
            String act = it.next();
            boolean encontrado = false;
            ListaSimple<Entrega>.Iterador itE = entregas.iterador();
            while (itE.hasNext()) {
                Entrega e = itE.next();
                if (e.getNombreActividad().equals(act) && e.estaCalificada()) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) return false;
        }
        return true;
    }

    public boolean involucraActividad(String nombreActividad) {
        return actividades.contiene(nombreActividad);
    }

    private boolean esOperador(String t) {
        return t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/");
    }

    private boolean esNumero(String t) {
        if (t == null || t.isEmpty()) return false;
        try {
            Double.parseDouble(t);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double buscarNota(NodoCompuesto<Estudiante, Entrega> nodo, String act) {
        ListaSimple<Entrega> entregas = nodo.getReferenciaLista();
        if (entregas == null) return -1.0;
        ListaSimple<Entrega>.Iterador it = entregas.iterador();
        while (it.hasNext()) {
            Entrega e = it.next();
            if (e.getNombreActividad().equals(act) && e.estaCalificada()) {
                return e.getNota();
            }
        }
        return -1.0;
    }

    public String getNombre() {
        return nombre;
    }

    public String getExpresionOriginal() {
        return expresion;
    }

    public ListaSimple<String> getActividadesAsociadas() {
        return actividades;
    }

    public String getExpresionPostfija() {
        String res = "";
        for (int i = 0; i < pasos.length; i++) {
            if (i > 0) res += " ";
            res += pasos[i];
        }
        return res;
    }

    public String toString() {
        return "Calculo[" + nombre + " | " + expresion + "]";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return nombre.equals(((Calculo) o).nombre);
    }

    public int hashCode() {
        return nombre.hashCode();
    }
}
