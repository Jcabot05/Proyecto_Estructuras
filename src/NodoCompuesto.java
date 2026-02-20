/**
 * Nodo generico de la ListaCompuesta.
 * Cada nodo guarda un dato principal de tipo E y puede tener
 * una lista secundaria de elementos de tipo F.
 *
 * @param <E> Tipo del dato principal (ej. Estudiante, Actividad)
 * @param <F> Tipo de los datos en la lista secundaria (ej. Entrega)
 */
public class NodoCompuesto<E, F> {
    private E data;
    private NodoCompuesto<E, F> next;
    private ListaSimple<F> referenciaLista; // lista secundaria correctamente tipada como F

    public NodoCompuesto(E dato) {
        this.data = dato;
        this.next = null;
        this.referenciaLista = null;
    }

    // ---------- Getters ----------

    public E getData() {
        return this.data;
    }

    public NodoCompuesto<E, F> getNext() {
        return this.next;
    }

    /** Retorna la lista secundaria de este nodo (puede ser null si no se ha inicializado). */
    public ListaSimple<F> getReferenciaLista() {
        return this.referenciaLista;
    }

    // ---------- Setters ----------

    public void setData(E data) {
        this.data = data;
    }

    public void setNext(NodoCompuesto<E, F> nodo) {
        this.next = nodo;
    }

    public void setReferenciaLista(ListaSimple<F> lista) {
        this.referenciaLista = lista;
    }
}
