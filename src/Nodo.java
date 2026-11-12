public class Nodo<T> {
    private T etiqueta;
    private int indice;
    private int hermanoDer;
    private int hijoIzq;

    public Nodo(T etiqueta){
        this.etiqueta = etiqueta;
        hermanoDer = -1;
        hijoIzq = -1;
    }
    
    public T getEtiqueta(){return etiqueta;}

    public void setIndice(int i){indice = i;}
    public int getIndice(){return indice;}

    public void setHermanoDer(int i){hermanoDer = i;}
    public int getHermanoDer(){return hermanoDer;}

    public void setHijoIzq(int i){hijoIzq = i;}
    public int getHijoIzq(){return hijoIzq;}


    @Override
    public String toString() {
        return etiqueta.toString();
    }

}
