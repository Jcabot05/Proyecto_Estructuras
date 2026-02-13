import java.util.Comparator;

public class ListaCompuesta<E, F> {
    private NodoCompuesto<E, F> header;
    private NodoCompuesto<E, F> tail;
    private int size;

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

    public boolean isEmpty() {
        return size == 0;
    }

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

    public void add(E elemento) {
        add(new NodoCompuesto<E, F>(elemento));
    }

    public void addElementInSecondaryList(NodoCompuesto<E, F> nodo, F elemento) {
        if (nodo.getReferenciaLista() == null) {
            nodo.setListaCompuesta(new ListaCompuesta<E, F>());
        }
        // Crear un nuevo nodo con el elemento F (cast necesario por limitaciones de genéricos)
        NodoCompuesto<E, F> nuevoNodo = new NodoCompuesto<>((E) elemento);
        nodo.getReferenciaLista().add(nuevoNodo);
    }

    public NodoCompuesto<E, F> buscarPrimero(Comparator<E> c, E data) {
        for (NodoCompuesto<E, F> p = header; p != null; p = p.getNext()) {
            E data1 = p.getData();
            if (c.compare(data1, data) < 0) {
                return p;
            }
        }
        return null;
    }

    public ListaCompuesta<E, F> buscarTodosMenoresEnListaSecundaria(Comparator<F> c, F data) {
        ListaCompuesta<E, F> nueva = new ListaCompuesta<>();
        
        for (NodoCompuesto<E, F> p = this.header; p != null; p = p.getNext()) {
            ListaCompuesta<E, F> sublista = p.getReferenciaLista();
            if (sublista != null) {
                // Buscar en la sublista usando un comparador adaptado
                boolean encontrado = false;
                for (NodoCompuesto<E, F> subNodo = sublista.getHeader(); subNodo != null; subNodo = subNodo.getNext()) {
                    // El dato en la sublista es de tipo E, pero representa a F
                    if (c.compare((F) subNodo.getData(), data) < 0) {
                        encontrado = true;
                        break;
                    }
                }
                if (encontrado) {
                    nueva.add(new NodoCompuesto<E, F>(p.getData()));
                }
            }
        }
        return nueva;
    }

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
