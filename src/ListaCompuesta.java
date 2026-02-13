import java.util.Comparator;

/**
 * TDA ListaCompuesta
 * Lista enlazada donde cada nodo puede tener una lista secundaria asociada
 * @param <E> Tipo del dato principal de los nodos
 * @param <F> Tipo de los elementos de las listas secundarias
 */
public class ListaCompuesta<E, F> {
    private NodoCompuesto<E, F> header;
    private NodoCompuesto<E, F> tail;
    private int size;

    /**
     * Constructor
     */
    public ListaCompuesta() {
        this.header = null;
        this.tail = null;
        this.size = 0;
    }

    // Getters y Setters
    public NodoCompuesto<E, F> getHeader() {
        return this.header;
    }

    public NodoCompuesto<E, F> getTail() {
        return this.tail;
    }

    public int getSize() {
        return this.size;
    }

    public void setHeader(NodoCompuesto<E, F> nodo) {
        this.header = nodo;
    }

    public void setTail(NodoCompuesto<E, F> nodo) {
        this.tail = nodo;
    }

    /**
     * Verifica si la lista está vacía
     * @return true si la lista está vacía
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Inserta un nodo al final de la lista principal
     * @param nodo Nodo a insertar
     */
    public void add(NodoCompuesto<E, F> nodo) {
        if (size == 0) {
            setHeader(nodo);
            setTail(nodo);
        } else {
            getTail().setNext(nodo);
            setTail(nodo);
        }
        size++;
    }

    /**
     * Inserta un elemento al final de la lista principal (crea el nodo automáticamente)
     * @param elemento Elemento a insertar
     */
    public void add(E elemento) {
        add(new NodoCompuesto<E, F>(elemento));
    }

    /**
     * Inserta un elemento al final de la lista secundaria de un nodo
     * @param nodo Nodo al que se le agregará el elemento en su lista secundaria
     * @param elemento Elemento a agregar
     */
    public void addElementInSecondaryList(NodoCompuesto<E, F> nodo, F elemento) {
        if (nodo.getReferenciaLista() == null) {
            nodo.setListaCompuesta(new ListaCompuesta<E, F>());
        }
        nodo.getReferenciaLista().add(new NodoCompuesto<E, F>(elemento));
    }

    /**
     * Busca el primer elemento que cumple con el criterio del comparador
     * @param c Comparador a utilizar
     * @param data Dato a comparar
     * @return Primer nodo que cumple la condición, o null si no se encuentra
     */
    public NodoCompuesto<E, F> buscarPrimero(Comparator<E> c, E data) {
        for (NodoCompuesto<E, F> p = header; p != null; p = p.getNext()) {
            E data1 = p.getData();
            if (c.compare(data1, data) < 0) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca todos los nodos que tienen en su lista secundaria al menos un elemento menor
     * que el dato proporcionado según el comparador
     * @param c Comparador para elementos de tipo F
     * @param data Dato a comparar
     * @return Nueva lista con los nodos que cumplen la condición
     */
    public ListaCompuesta<E, F> buscarTodosMenoresEnListaSecundaria(Comparator<F> c, F data) {
        ListaCompuesta<E, F> nueva = new ListaCompuesta<>();

        for (NodoCompuesto<E, F> p = this.header; p != null; p = p.getNext()) {
            ListaCompuesta<E, F> sublista = p.getReferenciaLista();
            if (sublista != null && sublista.buscarPrimero(c, data) != null) {
                nueva.add(new NodoCompuesto<E, F>(p.getData()));
            }
        }
        return nueva;
    }

    /**
     * Obtiene un nodo en una posición específica
     * @param index Índice del nodo (comienza en 0)
     * @return Nodo en la posición indicada, o null si el índice es inválido
     */
    public NodoCompuesto<E, F> get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        NodoCompuesto<E, F> current = header;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current;
    }

    /**
     * Elimina un elemento de la lista principal
     * @param data Dato a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean remove(E data) {
        if (isEmpty()) {
            return false;
        }

        // Caso especial: eliminar el header
        if (header.getData().equals(data)) {
            header = header.getNext();
            if (header == null) {
                tail = null;
            }
            size--;
            return true;
        }

        // Buscar y eliminar
        NodoCompuesto<E, F> prev = header;
        NodoCompuesto<E, F> current = header.getNext();

        while (current != null) {
            if (current.getData().equals(data)) {
                prev.setNext(current.getNext());
                if (current == tail) {
                    tail = prev;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.getNext();
        }

        return false;
    }

    /**
     * Limpia toda la lista
     */
    public void clear() {
        header = null;
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        String texto = "";

        for (NodoCompuesto<E, F> nodo = this.getHeader(); nodo != null; nodo = nodo.getNext()) {
            texto = texto + "\n" + nodo.getData() + " ";
            if (nodo.getReferenciaLista() != null) {
                texto += "---->" + nodo.getReferenciaLista().toString();
            }
        }

        return texto;
    }
}
