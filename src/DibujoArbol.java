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
        setPreferredSize(new Dimension(900, 750));
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

                int altoNecesario = maxYArbol + 60 + 310 + 7*24;
                if (getPreferredSize().height != altoNecesario) {
                    setPreferredSize(new Dimension(Math.max(getWidth(), 800), altoNecesario));
                    revalidate();
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
                Object cursorObj = obtenerCursorSeguro();
                if (cursorObj == null) return;

                int tamanoArreglo = obtenerTamanoArregloCursorSeguro();
                if (tamanoArreglo <= 0) return;

                int anchoTabla = 480;
                int altoFila = 24;
                int xInicio = (getWidth() - anchoTabla) / 2;

                // INDICE | HIJO_IZQ | DATO | HERMANO_DER
                int colIndice = xInicio;
                int colHijo = xInicio + 75;
                int colDato = xInicio + 195;
                int colHerm = xInicio + 325;

                FontMetrics fmTitulo = g2d.getFontMetrics(new Font("Arial", Font.BOLD, 14));
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.setColor(new Color(33, 37, 41));

                
                int indiceRaiz = arbol.RAIZ();
                String tituloCursor = "CURSOR  |  RAIZ: " + indiceRaiz;
                int xTituloCursor = (getWidth() - fmTitulo.stringWidth(tituloCursor)) / 2;
                g2d.drawString(tituloCursor, xTituloCursor, yInicio - 8);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));

                int yCabecera = yInicio;
                g2d.drawRect(xInicio, yCabecera, anchoTabla, altoFila);
                g2d.drawString("i", colIndice + 8, yCabecera + 16);
                g2d.drawString("ETIQUETA", colDato + 8, yCabecera + 16);
                g2d.drawString("HIJO_IZQ", colHijo + 8, yCabecera + 16);
                g2d.drawString("HERMANO_DER", colHerm + 8, yCabecera + 16);

                g2d.drawLine(colDato, yCabecera, colDato, yCabecera + altoFila);
                g2d.drawLine(colHijo, yCabecera, colHijo, yCabecera + altoFila);
                g2d.drawLine(colHerm, yCabecera, colHerm, yCabecera + altoFila);

                g2d.setFont(new Font("Arial", Font.PLAIN, 12));

                int ultimoIndiceOcupado = -1;
                for (int i = 0; i < tamanoArreglo; i++) {
                    Nodo<String> n = obtenerNodoEnPosicionCursorSeguro(cursorObj, i);
                    if (n != null && n.getEtiqueta() != null) {
                        ultimoIndiceOcupado = i;
                    }
                }
                int limiteFilas = ultimoIndiceOcupado + 1;

                for (int i = 0; i < limiteFilas; i++) {
                    Nodo<String> nodoActual = obtenerNodoEnPosicionCursorSeguro(cursorObj, i);

                    String hijoStr = "-1";
                    String datoStr = "null";
                    String hermStr = "-1";

                    if (nodoActual != null) {
                        if (nodoActual.getEtiqueta() != null) {
                            datoStr = nodoActual.getEtiqueta();
                        }

                        hijoStr = String.valueOf(nodoActual.getHijoIzq());
                        hermStr = String.valueOf(nodoActual.getHermanoDer());
                    }

                    int yFila = yCabecera + ((i + 1) * altoFila);
                    g2d.drawRect(xInicio, yFila, anchoTabla, altoFila);
                    g2d.drawString(String.valueOf(i), colIndice + 8, yFila + 16);
                    g2d.drawString(datoStr, colDato + 8, yFila + 16);
                    g2d.drawString(hijoStr, colHijo + 8, yFila + 16);
                    g2d.drawString(hermStr, colHerm + 8, yFila + 16);
                    
                    g2d.drawLine(colDato, yFila, colDato, yFila + altoFila);
                    g2d.drawLine(colHijo, yFila, colHijo, yFila + altoFila);
                    g2d.drawLine(colHerm, yFila, colHerm, yFila + altoFila);
                }

                int yTituloPruebas = yCabecera + ((tamanoArreglo + 1) * altoFila) + 28;

                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.setColor(new Color(33, 37, 41));
                String tituloPruebas = "PRUEBAS";
                int xTituloPruebas = (getWidth() - fmTitulo.stringWidth(tituloPruebas)) / 2;
                g2d.drawString(tituloPruebas, xTituloPruebas, yTituloPruebas);

                int yRecorridos = yTituloPruebas + 22;
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));

                ArrayList<String> pre = arbol.ORD_PRE(raizCursor);
                String lineaPre = "Orden_Prev => " + pre;
                g2d.drawString(lineaPre, (getWidth() - g2d.getFontMetrics().stringWidth(lineaPre)) / 2, yRecorridos);

                ArrayList<String> sim = arbol.ORD_SIM(raizCursor);
                String lineaSim = "Orden_Sim => " + sim;
                g2d.drawString(lineaSim, (getWidth() - g2d.getFontMetrics().stringWidth(lineaSim)) / 2, yRecorridos + 20);

                ArrayList<String> pos = arbol.ORD_POST(raizCursor);
                String lineaPos = "Orden_Pos => " + pos;
                g2d.drawString(lineaPos, (getWidth() - g2d.getFontMetrics().stringWidth(lineaPos)) / 2, yRecorridos + 40);

                String lineaNiv = "Niveles del Arbol => " + nivelesY.size();
                g2d.drawString(lineaNiv, (getWidth() - g2d.getFontMetrics().stringWidth(lineaNiv)) / 2, yRecorridos + 60);

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

    private Object obtenerCursorSeguro() {
        try {
            java.lang.reflect.Field campoMemoria = Arbol.class.getDeclaredField("memoria");
            campoMemoria.setAccessible(true);
            return campoMemoria.get(arbol);
        } catch (Exception e) {
            return null;
        }
    }

    private int obtenerTamanoArregloCursorSeguro() {
        try {
            Object cursorObj = obtenerCursorSeguro();
            if (cursorObj == null) return 0;

            java.lang.reflect.Field campoArreglo = cursorObj.getClass().getDeclaredField("memoria");
            campoArreglo.setAccessible(true);
            Object[] arreglo = (Object[]) campoArreglo.get(cursorObj);
            return arreglo != null ? arreglo.length : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private Nodo<String> obtenerNodoEnPosicionCursorSeguro(Object cursorObj, int posicion) {
        try {
            java.lang.reflect.Field campoArreglo = cursorObj.getClass().getDeclaredField("memoria");
            campoArreglo.setAccessible(true);
            Object[] arreglo = (Object[]) campoArreglo.get(cursorObj);
            if (arreglo != null && posicion >= 0 && posicion < arreglo.length) {
                return (Nodo<String>) arreglo[posicion];
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
