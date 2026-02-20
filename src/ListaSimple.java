/**
 * Lista enlazada simple generica.
 * Usada como lista secundaria dentro de cada NodoCompuesto.
 * Evita los cast inseguros del diseno anterior.
 *
 * @param <T> Tipo de los elementos almacenados
 */
public class ListaSimple<T> {

    /** Nodo interno de la ListaSimple. */
    private static class Nodo<T> {
        T data;
        Nodo<T> next;

        Nodo(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Nodo<T> head;
    private Nodo<T> tail;
    private int size;

    public ListaSimple() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // ---------- Operaciones basicas ----------

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    /** Agrega un elemento al final de la lista. */
    public void add(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (head == null) {
            head = nuevo;
            tail = nuevo;
        } else {
            tail.next = nuevo;
            tail = nuevo;
        }
        size++;
    }

    /**
     * Retorna el elemento en la posicion indicada (0-indexado).
     * Retorna null si el indice esta fuera de rango.
     */
    public T get(int index) {
        if (index < 0 || index >= size) return null;
        Nodo<T> actual = head;
        for (int i = 0; i < index; i++) {
            actual = actual.next;
        }
        return actual.data;
    }

    /** Itera desde el primer elemento. Retorna null cuando no hay mas. */
    public T getFirst() {
        return head != null ? head.data : null;
    }

    // ---------- Iteracion manual (compatible con for sin Iterator) ----------

    /** Clase auxiliar para iterar manualmente sobre la lista. */
    public class Iterador {
        private Nodo<T> actual;

        public Iterador() {
            this.actual = head;
        }

        public boolean hasNext() {
            return actual != null;
        }

        public T next() {
            T dato = actual.data;
            actual = actual.next;
            return dato;
        }
    }

    public Iterador iterador() {
        return new Iterador();
    }

    // ---------- Operaciones de conjuntos ----------

    /**
     * Union de esta lista con otra, sin repetidos.
     * Usa equals() para comparar duplicados.
     */
    public ListaSimple<T> union(ListaSimple<T> otra) {
        ListaSimple<T> resultado = new ListaSimple<>();
        // Agregar todos los de esta lista
        for (Nodo<T> p = head; p != null; p = p.next) {
            resultado.add(p.data);
        }
        // Agregar de la otra solo si no estan ya
        for (Nodo<T> p = otra.head; p != null; p = p.next) {
            if (!resultado.contiene(p.data)) {
                resultado.add(p.data);
            }
        }
        return resultado;
    }

    /**
     * Interseccion de esta lista con otra.
     * Retorna elementos que aparecen en ambas.
     */
    public ListaSimple<T> interseccion(ListaSimple<T> otra) {
        ListaSimple<T> resultado = new ListaSimple<>();
        for (Nodo<T> p = head; p != null; p = p.next) {
            if (otra.contiene(p.data)) {
                resultado.add(p.data);
            }
        }
        return resultado;
    }

    /** Indica si la lista contiene el elemento dado (usa equals). */
    public boolean contiene(T elemento) {
        for (Nodo<T> p = head; p != null; p = p.next) {
            if (p.data.equals(elemento)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo<T> p = head; p != null; p = p.next) {
            sb.append(p.data);
            if (p.next != null) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
