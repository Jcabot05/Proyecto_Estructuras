import java.util.Comparator;

/**
 * TDA ListaCompuesta: lista enlazada de nodos principales (tipo E),
 * donde cada nodo puede tener una lista secundaria de elementos (tipo F).
 *
 * Ejemplo de uso:
 *   ListaCompuesta&lt;Estudiante, Entrega&gt; - cada estudiante tiene sus entregas
 *   ListaCompuesta&lt;Actividad, Entrega&gt;  - cada actividad tiene sus entregas
 *
 * @param <E> Tipo del elemento principal (ej. Estudiante, Actividad, Calculo)
 * @param <F> Tipo de los elementos en la lista secundaria (ej. Entrega)
 */
public class ListaCompuesta<E, F> {

    private NodoCompuesto<E, F> header;
    private NodoCompuesto<E, F> tail;
    private int size;

    public ListaCompuesta() {
        this.header = null;
        this.tail = null;
        this.size = 0;
    }

    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public NodoCompuesto<E, F> getHeader() { return header; }
    public NodoCompuesto<E, F> getTail()   { return tail; }
    public int getSize()                   { return size; }
    public boolean isEmpty()               { return size == 0; }

    // =========================================================
    // OPERACIONES PRINCIPALES
    // =========================================================

    /**
     * Agrega un nodo ya construido al final de la lista principal.
     */
    public void add(NodoCompuesto<E, F> nodo) {
        if (size == 0) {
            header = nodo;
            tail = nodo;
        } else {
            tail.setNext(nodo);
            tail = nodo;
        }
        size++;
    }

    /**
     * Crea un nodo con el elemento dado y lo agrega al final de la lista principal.
     */
    public void add(E elemento) {
        add(new NodoCompuesto<>(elemento));
    }

    /**
     * Agrega un elemento F a la lista secundaria del nodo dado.
     * Si el nodo no tiene lista secundaria, la crea primero.
     */
    public void addElementInSecondaryList(NodoCompuesto<E, F> nodo, F elemento) {
        if (nodo.getReferenciaLista() == null) {
            nodo.setReferenciaLista(new ListaSimple<>());
        }
        nodo.getReferenciaLista().add(elemento);
    }

    /**
     * Retorna el nodo en la posicion dada (0-indexado).
     * Retorna null si el indice esta fuera de rango.
     */
    public NodoCompuesto<E, F> get(int index) {
        if (index < 0 || index >= size) return null;
        NodoCompuesto<E, F> actual = header;
        for (int i = 0; i < index; i++) {
            actual = actual.getNext();
        }
        return actual;
    }

    /**
     * Busca el primer nodo cuyo dato principal satisfaga el comparador
     * (retorna cuando compare devuelve 0).
     */
    public NodoCompuesto<E, F> buscarPrimero(Comparator<E> c, E data) {
        for (NodoCompuesto<E, F> p = header; p != null; p = p.getNext()) {
            if (c.compare(p.getData(), data) == 0) {
                return p;
            }
        }
        return null;
    }

    /**
     * Retorna la union de las listas secundarias de dos nodos, sin repetidos.
     * Util para combinar entregas de dos actividades distintas.
     */
    public ListaSimple<F> unionSecundarias(NodoCompuesto<E, F> nodo1,
                                            NodoCompuesto<E, F> nodo2) {
        ListaSimple<F> lista1 = nodo1.getReferenciaLista();
        ListaSimple<F> lista2 = nodo2.getReferenciaLista();

        if (lista1 == null && lista2 == null) return new ListaSimple<>();
        if (lista1 == null) return lista2;
        if (lista2 == null) return lista1;

        return lista1.union(lista2);
    }

    /**
     * Retorna la interseccion de las listas secundarias de dos nodos.
     */
    public ListaSimple<F> interseccionSecundarias(NodoCompuesto<E, F> nodo1,
                                                   NodoCompuesto<E, F> nodo2) {
        ListaSimple<F> lista1 = nodo1.getReferenciaLista();
        ListaSimple<F> lista2 = nodo2.getReferenciaLista();

        if (lista1 == null || lista2 == null) return new ListaSimple<>();
        return lista1.interseccion(lista2);
    }

    /**
     * Elimina el nodo cuyo dato principal sea igual al dado (usa equals).
     * @return true si se elimino, false si no se encontro
     */
    public boolean remove(E data) {
        if (isEmpty()) return false;

        if (header.getData().equals(data)) {
            header = header.getNext();
            if (header == null) tail = null;
            size--;
            return true;
        }

        NodoCompuesto<E, F> prev = header;
        NodoCompuesto<E, F> actual = header.getNext();

        while (actual != null) {
            if (actual.getData().equals(data)) {
                prev.setNext(actual.getNext());
                if (actual == tail) tail = prev;
                size--;
                return true;
            }
            prev = actual;
            actual = actual.getNext();
        }
        return false;
    }

    public void clear() {
        header = null;
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (NodoCompuesto<E, F> nodo = header; nodo != null; nodo = nodo.getNext()) {
            sb.append("\n  ").append(nodo.getData());
            if (nodo.getReferenciaLista() != null && !nodo.getReferenciaLista().isEmpty()) {
                sb.append(" --> ").append(nodo.getReferenciaLista());
            }
        }
        return sb.toString();
    }
}
