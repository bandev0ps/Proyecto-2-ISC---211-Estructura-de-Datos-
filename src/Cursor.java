public class Cursor<T> {

    private Nodo<T>[] memoria;
    private int raiz;
    private int tamano;

    private final int TAMANO = 10;

    public int getTamano(){return tamano;}

    public Cursor(){
        memoria = (Nodo<T>[]) new Nodo[TAMANO];
        raiz = -1;
        tamano = 0;

        for (int i = 0; i < TAMANO; i++) {
            memoria[i] = new Nodo<T>(null);
            memoria[i].setIndice(i);
        }

    }

    private void ajustarMemoria(){
        int tamañoViejo = memoria.length;
        int nuevoTamaño = tamañoViejo *2;
        Nodo<T>[] nuevaMemoria = (Nodo<T>[]) new Nodo[nuevoTamaño];
        
        for(int i=0; i<tamañoViejo; i++){
            nuevaMemoria[i] = memoria[i];
        }

        for (int i = tamañoViejo; i < nuevoTamaño - 1; i++) {
            nuevaMemoria[i] = new Nodo<>(null);
            nuevaMemoria[i].setIndice(i);
        }

        memoria = nuevaMemoria;

    }

    private int buscarMemoriaDisponible(){
        for(int i=0; i<memoria.length;i++){
            if (memoria[i].getEtiqueta() == null){return i;}
        }
        return -1;
    }

    public void insertarNodo(Nodo<T> nodo){
        int posicionDisponible = buscarMemoriaDisponible();

        if (posicionDisponible==-1) {
            ajustarMemoria();
            posicionDisponible = buscarMemoriaDisponible();
        }

        memoria[posicionDisponible] = nodo;

        nodo.setIndice(posicionDisponible);
        tamano++;
        System.out.println("Nodo insertado exitosamente");
    }

    public int buscarIndice(T etiqueta, int indiceActual){
        if (etiqueta == null || indiceActual == -1) {return -1;}

        if (memoria[indiceActual].getEtiqueta().equals(etiqueta)) {
            return indiceActual;
        }

        int buscarHijo = buscarIndice(etiqueta, memoria[indiceActual].getHijoIzq());
        if (buscarHijo!=-1) {return buscarHijo;}

        return buscarIndice(etiqueta, memoria[indiceActual].getHermanoDer());
    }

    public Nodo<T> getNodo(int indice) throws RangoInvalidoException{
        if (indice < 0 || indice >= memoria.length) {
            throw new RangoInvalidoException("Indice invalido.");
        }

        for(int i=0; i<memoria.length; i++){
            if (memoria[i] != null && memoria[i].getIndice() == indice) {
                return memoria[i];
            }
        }

        return null;
    }


}
