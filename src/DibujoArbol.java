import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DibujoArbol extends JPanel {
    private Arbol<String> arbol;
    private int raizCursor;

    public DibujoArbol(Arbol<String> arbol, int raizCursor) {
        this.arbol = arbol;
        this.raizCursor = raizCursor;
        setBackground(Color.WHITE); // Fondo blanco limpio
    }

    public void actualizarArbol(int nuevaRaizCursor) {
        this.raizCursor = nuevaRaizCursor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            if (raizCursor != -1) {
                int anchoPanel = getWidth();
                dibujarSubarbol(g2d, raizCursor, anchoPanel / 2, 50, anchoPanel / 3);
            } else {
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.ITALIC, 14));
                g2d.drawString("El árbol está vacío", getWidth() / 2 - 60, getHeight() / 2);
            }
        } catch (Exception e) {
            g2d.setColor(Color.RED);
            g2d.drawString("Error al dibujar: " + e.getMessage(), 20, 20);
        }
    }

    private void dibujarSubarbol(Graphics2D g2d, int nodoCursor, int x, int y, int anchoDisponible) {
        if (nodoCursor == -1) return;

        try {
            ArrayList<String> datosNodo = null;
            try {
                datosNodo = arbol.ORD_PRE(nodoCursor);
            } catch (RangoInvalidoException e) {
                return;
            }

            if (datosNodo == null || datosNodo.isEmpty()) return;
            String etiqueta = datosNodo.get(0);

            int hijoActual = -1;
            try {
                hijoActual = arbol.HIJO_MAS_IZQ(nodoCursor);
            } catch (RangoInvalidoException ignored) {}

            java.util.ArrayList<Integer> listaHijos = new java.util.ArrayList<>();
            int tempHijo = hijoActual;
            while (tempHijo != -1) {
                listaHijos.add(tempHijo);
                try {
                    tempHijo = obtenerHermanoDerechoSeguro(tempHijo);
                } catch (Exception e) {
                    break;
                }
            }

            int numHijos = listaHijos.size();

            if (numHijos > 0) {
                int desplazamientoX = anchoDisponible / Math.max(numHijos, 1);
                int inicioX = x - (anchoDisponible / 2) + (desplazamientoX / 2);
                int yHijo = y + 70; 

                for (int i = 0; i < numHijos; i++) {
                    int hijoCursorId = listaHijos.get(i);
                    int xHijoCalculado = inicioX + (i * desplazamientoX);
                    
                    // Línea conectora gris elegante
                    g2d.setColor(new Color(120, 144, 156));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(x, y, xHijoCalculado, yHijo);

                    // Llamada recursiva para los hijos
                    dibujarSubarbol(g2d, hijoCursorId, xHijoCalculado, yHijo, desplazamientoX);
                }
            }

            int radio = 22;
            g2d.setColor(new Color(33, 150, 243));
            g2d.fillOval(x - radio, y - radio, radio * 2, radio * 2);
            
            g2d.setColor(new Color(13, 71, 161));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - radio, y - radio, radio * 2, radio * 2);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            FontMetrics fm = g2d.getFontMetrics();
            int anchoTexto = fm.stringWidth(etiqueta);
            int altoTexto = fm.getAscent();
            
            g2d.drawString(etiqueta, x - (anchoTexto / 2), y + (altoTexto / 4));

        } catch (Exception e) {
        }
    }
    @SuppressWarnings("unchecked")
    private int obtenerHermanoDerechoSeguro(int indice) {
        try {
            java.lang.reflect.Field campoMemoria = Arbol.class.getDeclaredField("memoria");
            campoMemoria.setAccessible(true);
            Object cursorObj = campoMemoria.get(arbol);
            
            java.lang.reflect.Method metodoGetNodo = cursorObj.getClass().getMethod("getNodo", int.class);
            Nodo<String> nodo = (Nodo<String>) metodoGetNodo.invoke(cursorObj, indice);
            return nodo != null ? nodo.getHermanoDer() : -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
