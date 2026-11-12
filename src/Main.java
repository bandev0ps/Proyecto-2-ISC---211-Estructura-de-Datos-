public class Main {

    public static void main(String[] args) {

        try {
            Arbol<String> arbolito = new Arbol<String>();
            arbolito.CREA("Raiz", arbolito.CREA("A", arbolito.CREA("C"), arbolito.CREA("D")),
            arbolito.CREA("B"));

            var t = arbolito.ORD_PRE(arbolito.RAIZ());
            System.out.println("Orden Previo");
            for (String ele : t) {
                System.out.println(ele);
            }
            
        } catch (RangoInvalidoException e) {
            System.out.println(e.getMessage());
        }
    }

}
