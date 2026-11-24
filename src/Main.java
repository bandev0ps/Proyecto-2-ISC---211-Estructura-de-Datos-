import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        try {
            Arbol<String> arbolito = new Arbol<String>();
            //arbolito.CREA("Pebbles", arbolito.CREA("Pedro", arbolito.CREA0("Ed"), arbolito.CREA0("Edna")),
            //arbolito.CREA("Vilma", arbolito.CREA("Perla")));

            arbolito.CREA("z", arbolito.CREA("X",arbolito.CREA0("y"),arbolito.CREA("R",
                    arbolito.CREA0("Q"),arbolito.CREA0("G"))),arbolito.CREA("L",arbolito.CREA0("S")),arbolito.CREA0("M"),
            arbolito.CREA("K",arbolito.CREA0("T"),arbolito.CREA("U",arbolito.CREA0("F"),arbolito.CREA0("V"),
                    arbolito.CREA0("E"), arbolito.CREA("D", arbolito.CREA0("A"), arbolito.CREA0("B"), arbolito.CREA0("C"))), arbolito.CREA0("H")),
                    arbolito.CREA("j",arbolito.CREA0("I")));


            System.out.println("Raiz: " + arbolito.RAIZ());

            var t = arbolito.ORD_PRE(arbolito.RAIZ());
            System.out.println("Orden Previo");
            for (String ele : t) {
                System.out.println(ele);
            }

            var t2 = arbolito.ORD_SIM(arbolito.RAIZ());
            System.out.println("\n Orden Simetrico (INORDEN)");
            for (String ele : t2) {
                System.out.println(ele);
            }

            var t3 = arbolito.ORD_POST(arbolito.RAIZ());
            System.out.println("\nOrden Posterior");
            for (String ele : t3) {
                System.out.println(ele);
            }

            var hijoIzq = arbolito.HIJO_MAS_IZQ(arbolito.RAIZ());
            System.out.println("\nHijo mas izquierdo de la raiz: " + arbolito.ETIQUETA(hijoIzq));
            var hermanoDer = arbolito.HERMANO_DER(hijoIzq);
            System.out.println("Hermano derecho del hijo mas izquierdo: " + arbolito.ETIQUETA(hermanoDer));

            SwingUtilities.invokeLater(() -> {
                JFrame ventana = new JFrame("Visualizador de Árbol General - Estructura de Datos");
                ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ventana.setSize(900, 650);
                ventana.setLocationRelativeTo(null);
                DibujoArbol panelDibujo = new DibujoArbol(arbolito, arbolito.RAIZ());
                JScrollPane scrollPane = new JScrollPane(panelDibujo);
                ventana.add(scrollPane, BorderLayout.CENTER);

                //ventana.add(panelDibujo);
                ventana.setVisible(true);
            });
        } catch (RangoInvalidoException e) {
            System.out.println(e.getMessage());
        }
    }

}
