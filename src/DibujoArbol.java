import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DibujoArbol extends JPanel {
    private Arbol<String> arbol;
    private int raizCursor;
    private Map<Integer, Integer> nivelesY = new TreeMap<>();

    public DibujoArbol(Arbol<String> arbol, int raizCursor) {
        this.arbol = arbol;
        this.raizCursor = raizCursor;
        setBackground(Color.WHITE); 
    }

    public void actualizarArbol(int nuevaRaizCursor) {
        this.raizCursor = nuevaRaizCursor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        nivelesY.clear();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            if (raizCursor != -1) {
                int anchoPanel = getWidth();
                dibujarSubarbol(g2d, raizCursor, anchoPanel / 2, 50, anchoPanel / 3, 0);
                int radio = 22;
                for (Map.Entry<Integer, Integer> entry : nivelesY.entrySet()) {
                    int yCentroNodo = entry.getValue();
                    int nivel = entry.getKey();
                    int yLinea = yCentroNodo + radio;

                    g2d.setColor(Color.black); 
                    g2d.setStroke(new BasicStroke(1f));
                    g2d.drawLine(70, yLinea, getWidth() - 30, yLinea);

                    g2d.setColor(Color.black);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString("Nivel " + nivel, 10, yLinea + 4);
                }
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

    private void dibujarSubarbol(Graphics2D g2d, int nodoCursor, int x, int y, int anchoDisponible, int nivel) {
        if (nodoCursor == -1) return;
        nivelesY.put(nivel, y);

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
                    
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(x, y, xHijoCalculado, yHijo);

                    dibujarSubarbol(g2d, hijoCursorId, xHijoCalculado, yHijo, desplazamientoX, nivel + 1);
                }
            }

            int radio = 22;
            g2d.setColor(new Color(76, 175, 80));
            g2d.fillOval(x - radio, y - radio, radio * 2, radio * 2);
            
            g2d.setColor(new Color(46, 125, 50));
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
