import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DibujoArbol extends JPanel {
    private Arbol<String> arbol;
    private int raizCursor;
    private Map<Integer, Integer> nivelesY = new TreeMap<>();
    private int maxYArbol = 50;

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
        maxYArbol = 50;
        
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

                dibujarCursorDebajo(g2d, maxYArbol + 60);
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
        if (y > maxYArbol) {
            maxYArbol = y;
        }

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

    private void dibujarCursorDebajo(Graphics2D g2d, int yInicio) {
        try {
            ArrayList<String> nodos = arbol.ORD_PRE(raizCursor);
            if (nodos == null || nodos.isEmpty()) return;

            int xInicio = 70;
            int colNodo = 70;
            int colHijo = 190;
            int colHerm = 330;
            int anchoTabla = 400;
            int altoFila = 24;

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            int yCabecera = yInicio;
            g2d.drawRect(xInicio, yCabecera, anchoTabla, altoFila);
            g2d.drawString("NODO", colNodo + 8, yCabecera + 16);
            g2d.drawString("HIJO_MAS_IZQ", colHijo + 8, yCabecera + 16);
            g2d.drawString("HERMANO_DER", colHerm + 8, yCabecera + 16);

            g2d.drawLine(colHijo, yCabecera, colHijo, yCabecera + altoFila);
            g2d.drawLine(colHerm, yCabecera, colHerm, yCabecera + altoFila);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (int i = 0; i < nodos.size(); i++) {
                String etiquetaNodo = nodos.get(i);
                int idxNodo = obtenerIndicePorEtiquetaSeguro(etiquetaNodo);

                String hijoStr = "-1";
                String hermStr = "-1";

                if (idxNodo != -1) {
                    int idxHijo = arbol.HIJO_MAS_IZQ(idxNodo);
                    if (idxHijo != -1) {
                        String etiqHijo = arbol.ETIQUETA(idxHijo);
                        if (etiqHijo != null) {
                            hijoStr = etiqHijo;
                        }
                    }

                    int idxHerm = arbol.HERMANO_DER(idxNodo);
                    if (idxHerm != -1) {
                        String etiqHerm = arbol.ETIQUETA(idxHerm);
                        if (etiqHerm != null) {
                            hermStr = etiqHerm;
                        }
                    }
                }

                int yFila = yCabecera + ((i + 1) * altoFila);
                g2d.drawRect(xInicio, yFila, anchoTabla, altoFila);
                g2d.drawString(etiquetaNodo, colNodo + 8, yFila + 16);
                g2d.drawString(hijoStr, colHijo + 8, yFila + 16);
                g2d.drawString(hermStr, colHerm + 8, yFila + 16);

                g2d.drawLine(colHijo, yFila, colHijo, yFila + altoFila);
                g2d.drawLine(colHerm, yFila, colHerm, yFila + altoFila);
            }

            int yRecorridos = yCabecera + ((nodos.size() + 2) * altoFila);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            ArrayList<String> pre = arbol.ORD_PRE(raizCursor);
            g2d.drawString("Orden_Prev => " + pre, xInicio, yRecorridos);

            ArrayList<String> sim = arbol.ORD_SIM(raizCursor);
            g2d.drawString("Orden_Sim => " + sim, xInicio, yRecorridos + 20);

            ArrayList<String> pos = arbol.ORD_POST(raizCursor);
            g2d.drawString("Orden_Pos => " + pos, xInicio, yRecorridos + 40);

            g2d.drawString("Niveles del Arbol => " + nivelesY.size(), xInicio, yRecorridos + 60);

        } catch (Throwable e) {
            g2d.setColor(Color.RED);
            g2d.drawString("Error tabla: " + e.getMessage(), 70, yInicio + 20);
        }
    }

    private int obtenerIndicePorEtiquetaSeguro(String etiqueta) {
        try {
            java.lang.reflect.Field campoMemoria = Arbol.class.getDeclaredField("memoria");
            campoMemoria.setAccessible(true);
            Object cursorObj = campoMemoria.get(arbol);

            java.lang.reflect.Method metodoBuscar = cursorObj.getClass().getMethod("buscarIndice", Object.class, int.class);
            return (int) metodoBuscar.invoke(cursorObj, etiqueta, raizCursor);
        } catch (Exception e) {
            return -1;
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
