
public class NodoCompuesto<E, F> {
    private E data;
    private NodoCompuesto<E, F> next;
    private ListaCompuesta<E, F> referenciaLista;


    public NodoCompuesto(E dato) {
        this.data = dato;
        this.next = null;
        this.referenciaLista = null;
    }

    public NodoCompuesto<E, F> getNext() {
        return this.next;
    }

    public E getData() {
        return this.data;
    }

    public ListaCompuesta<E, F> getReferenciaLista() {
        return this.referenciaLista;
    }

    public void setNext(NodoCompuesto<E, F> nodo) {
        this.next = nodo;
    }

    public void setData(E data) {
        this.data = data;
    }

    public void setListaCompuesta(ListaCompuesta<E, F> lista) {
        this.referenciaLista = lista;
    }
}
