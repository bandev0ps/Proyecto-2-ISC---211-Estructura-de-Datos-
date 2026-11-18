import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

public class Arbol<T> {
    private int raiz;
    private Cursor<T> memoria;

    Arbol(){
        memoria = new Cursor<T>();
        raiz = -1;
    }

    //---------------------------------------------------------------------------------------------

    // ----------------- OPERACIONES DEL TDA ARBOL -----------------

    // --- OPERACION RAIZ ---
    
    public int RAIZ(){return raiz;}

    // --- OPERACION CREA ---

    // CASO BASE
    public int CREA(T etiqueta){
        Nodo<T> nuevoNodo = new Nodo<T>(etiqueta);

        memoria.insertarNodo(nuevoNodo);
        int indiceNodo = nuevoNodo.getIndice();

        if (raiz == -1) {raiz = indiceNodo;}

        return indiceNodo;
    }

    // CASO RECURSIVO
    public int CREA(T etiqueta, int ... args ) throws RangoInvalidoException{
        Nodo<T> nuevoNodo = new Nodo<T>(etiqueta);

        memoria.insertarNodo(nuevoNodo);
        int indiceNodo = nuevoNodo.getIndice();

        Nodo<T> padre = memoria.getNodo(indiceNodo);
        padre.setHijoIzq(args[0]);

        int actual = args[0];
        for(int i = 1; i < args.length; i++){
            int hijoSiguiente = args[i];
            memoria.getNodo(actual).setHermanoDer(hijoSiguiente);
            actual = hijoSiguiente;
        }

        memoria.getNodo(actual).setHermanoDer(-1);

        raiz = indiceNodo;

        return indiceNodo;
    }

    // --- OPERACION PADRE ---

    public int PADRE(int nodo) throws RangoInvalidoException {
        memoria.getNodo(nodo);
        if (nodo == raiz || nodo == -1) {return -1;}
        return buscarPadre(raiz, nodo);
    }

    private int buscarPadre(int actual, int n) throws RangoInvalidoException {
        if (actual == -1) { return -1; }

        Nodo<T> nodoActual = memoria.getNodo(actual);
        if (nodoActual == null || nodoActual.getEtiqueta() == null) { return -1; }

        int hijo = nodoActual.getHijoIzq();
        while (hijo != -1) {
            if (hijo == n) {                
                return actual;
            }
            int resultado = buscarPadre(hijo, n);   
            if (resultado != -1) {
                return resultado;
            }
            hijo = memoria.getNodo(hijo).getHermanoDer();
        }
        return -1;
    }

    // --- OPERACION HIJO_MAS_IZQ ---

    public int HIJO_MAS_IZQ(int nodo) throws RangoInvalidoException{
        Nodo<T> nodoActual = memoria.getNodo(nodo);
        if (nodoActual == null || nodoActual.getEtiqueta() == null) { return -1; }
        return nodoActual.getHijoIzq();
    }

    // --- OPERACION HERMANO DERECHO ---

    public int HERMANO_DER(int nodo) throws RangoInvalidoException{
        Nodo<T> nodoActual = memoria.getNodo(nodo);
        if (nodoActual == null || nodoActual.getEtiqueta() == null) { return -1; }
        return nodoActual.getHermanoDer();
    }

    // --- OPERACION ETIQUETA ---
    
    public T ETIQUETA(int nodo) throws RangoInvalidoException{
        Nodo<T> nodoActual = memoria.getNodo(nodo);
        if (nodoActual == null || nodoActual.getEtiqueta() == null) { return null; }
        return nodoActual.getEtiqueta();
    }

    // --- OPERACION ANULA ---

    public void ANULA(int nodo) throws RangoInvalidoException{
        if (nodo == -1) {return;}

        Nodo<T> nodoActual = memoria.getNodo(nodo);
        if (nodoActual == null || nodoActual.getEtiqueta() == null) { return; }

        // Anula todos los hijos de izquierda a derecha
        int hijoActual = nodoActual.getHijoIzq();
        while (hijoActual != -1){
            ANULA(hijoActual);
            hijoActual = memoria.getNodo(hijoActual).getHermanoDer();
        }

        // Anula el nodo actual
        nodoActual.setHijoIzq(-1);
        nodoActual.setHermanoDer(-1);

        // Si el nodo anulado es la raíz, actualiza la raíz a -1
        if (nodo == raiz) {
            raiz = -1;
        }
    }

    // ---------- MOSTRAR NIVELES ----------

    public void MOSTRAR_NIVELES() throws RangoInvalidoException {
        if (raiz == -1) {
            System.out.println("El arbol esta vacio");
            return;
        }

        Queue<Integer> cola = new LinkedList<>();
        cola.add(raiz);
        int nivel = 0;

        while (!cola.isEmpty()) {
            int cantidad = cola.size();          // cuántos nodos tiene este nivel
            StringBuilder linea = new StringBuilder("Nivel " + nivel + ": ");

            for (int i = 0; i < cantidad; i++) {
                int actual = cola.remove();
                Nodo<T> nodo = memoria.getNodo(actual);
                linea.append(nodo.getEtiqueta()).append("  ");

                // encola todos los hijos: hijoIzq y luego sus hermanos derechos
                int hijo = nodo.getHijoIzq();
                while (hijo != -1) {
                    cola.add(hijo);
                    hijo = memoria.getNodo(hijo).getHermanoDer();
                }
            }

            System.out.println(linea);
            nivel++;
        }
    }

    //---------------------------------------------------------------------------------------------

    // ----------------- OPERACIONES DE RECORRIDO DE ARBOL -----------------

    // --- METODOS PARA ORDEN PREVIO ---

    private void orden_previo(int nodo, ArrayList<T> lista) throws RangoInvalidoException{
        if (nodo == -1) {return;}

        var papunodo = memoria.getNodo(nodo);
        if (papunodo == null || papunodo.getEtiqueta() == null) {return;}
        
        lista.add(papunodo.getEtiqueta());
        orden_previo(papunodo.getHijoIzq(), lista);
        orden_previo(papunodo.getHermanoDer(), lista);
    }

    public ArrayList<T> ORD_PRE(int nodo) throws RangoInvalidoException{
        ArrayList<T> orden = new ArrayList<T>();
        orden_previo(nodo, orden);
        return orden;
    }

    // --- METODOS PARA ORDEN SIMETRICO ---

    private void orden_Simetrico(int nodo, ArrayList<T> Lista) throws RangoInvalidoException{
        if (nodo == -1) {return;}

        var papupronodo = memoria.getNodo(nodo);
        if (papupronodo == null || papupronodo.getEtiqueta() == null) {return;}
        int hijoActual = papupronodo.getHijoIzq();

        if (hijoActual == -1){
            Lista.add(papupronodo.getEtiqueta());
            return;
        }

        // Visita simétricamente el primer hijo
        orden_Simetrico(hijoActual, Lista);

        // Visitar raíz actual
        Lista.add(papupronodo.getEtiqueta());

        hijoActual = memoria.getNodo(hijoActual).getHermanoDer();
        while (hijoActual != -1){
            orden_Simetrico(hijoActual, Lista);
            hijoActual = memoria.getNodo(hijoActual).getHermanoDer();
        }
    }

    public ArrayList<T> ORD_SIM(int nodo) throws RangoInvalidoException{
         ArrayList<T> orden = new ArrayList<T>();
         orden_Simetrico(nodo, orden);
         return orden;
    }

    // --- METODOS PARA ORDEN POSTERIOR ---

    private void orden_posterior(int nodo, ArrayList<T> lista) throws RangoInvalidoException{
        if (nodo == -1){return;}

        var papuhackernodo = memoria.getNodo(nodo);
        if (papuhackernodo == null || papuhackernodo.getEtiqueta() == null) { return; }

        // Visitar todos los hijos de izquierda a derecha
        int hijoActual = papuhackernodo.getHijoIzq();
        while (hijoActual != -1){
            orden_posterior(hijoActual, lista);
            hijoActual = memoria.getNodo(hijoActual).getHermanoDer();
        }
        
        // Por último la raíz
        lista.add(papuhackernodo.getEtiqueta());
    }

    public ArrayList<T> ORD_POST(int nodo) throws RangoInvalidoException{
        ArrayList<T> orden = new ArrayList<T>();
        orden_posterior(nodo, orden);
        return orden;
    }

    //---------------------------------------------------------------------------------------------
}
