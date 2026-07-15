package Utilidades;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class GeneradorFacturaImagen {

    private static final String OFICINA_DIRECCION = "Av. Los Pinos 456, San Isidro, Lima";
    private static final String TELEFONO_CONTACTO  = "(01) 456-7890";
    private static final String EMAIL_CONTACTO      = "contacto@clinicasanrafael.com";
    private static final String SITIO_WEB           = "www.clinicasanrafael.com";

    private static final Color FONDO = new Color(0xFC, 0xF7, 0xE8);
    private static final Color AZUL  = new Color(0x16, 0x14, 0xA6);

    private static final int W = 1000;
    private static final int H = 1150;
    private static final int MARGIN = 60;

    public static class ItemFactura {
        public final int cantidad;
        public final String producto;
        public final double precioUnitario;

        public ItemFactura(int cantidad, String producto, double precioUnitario) {
            this.cantidad = cantidad;
            this.producto = producto;
            this.precioUnitario = precioUnitario;
        }

        public double getTotal() { return cantidad * precioUnitario; }
    }

    public static BufferedImage generar(String nombreClinica, long numeroFactura, LocalDateTime fecha,
                                        String pacienteNombre, String pacienteDni, String pacienteTelefono,
                                        String motivo, List<ItemFactura> items) {

        double subtotal = items.stream().mapToDouble(ItemFactura::getTotal).sum();
        double impuestos = 0.0;
        double total = subtotal + impuestos;

        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.setColor(FONDO);
        g.fillRect(0, 0, W, H);
        g.setColor(AZUL);

        Font fBold   = new Font("DejaVu Sans", Font.BOLD, 20);
        Font fMono   = new Font("DejaVu Sans Mono", Font.PLAIN, 18);
        Font fMonoB  = new Font("DejaVu Sans Mono", Font.BOLD, 18);
        Font fTitulo = new Font("DejaVu Sans", Font.BOLD, 52);
        Font fSmall  = new Font("DejaVu Sans Mono", Font.PLAIN, 15);


        String iniciales = obtenerIniciales(nombreClinica);
        int boxSize = 88;
        int boxX = W - MARGIN - boxSize;
        int boxY = 55;
        g.setStroke(new BasicStroke(3f));
        g.draw(new RoundRectangle2D.Float(boxX, boxY, boxSize, boxSize, 6, 6));
        g.setFont(new Font("DejaVu Sans", Font.BOLD, 30));
        FontMetrics fmIni = g.getFontMetrics();
        int iniW = fmIni.stringWidth(iniciales);
        g.drawString(iniciales, boxX + (boxSize - iniW) / 2f, boxY + boxSize / 2f + 11);

        g.setFont(new Font("DejaVu Sans", Font.BOLD, 15));
        String nombreClinicaUpper = nombreClinica.toUpperCase();
        FontMetrics fmClinica = g.getFontMetrics();
        int nombreW = fmClinica.stringWidth(nombreClinicaUpper);
        g.drawString(nombreClinicaUpper, boxX + boxSize / 2f - nombreW / 2f, boxY + boxSize + 24);

        int y = 100;
        g.setFont(fTitulo);
        g.drawString("FACTURA", MARGIN, y);

        y += 42;
        g.setFont(fMono);
        g.drawString("Factura n.\u00b0", MARGIN, y);
        g.setFont(fMonoB);
        g.drawString(String.format("%04d", numeroFactura), MARGIN + 190, y);
        y += 28;
        g.setFont(fMono);
        g.drawString("Fecha", MARGIN, y);
        g.setFont(fMonoB);
        g.drawString(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yy")), MARGIN + 190, y);

        y += 30;
        g.setStroke(new BasicStroke(2.2f));
        g.drawLine(MARGIN, y, W - MARGIN, y);

        int leftColW = 330;
        int leftX = MARGIN;
        y += 55;
        g.setFont(fBold);
        g.drawString(pacienteNombre != null ? pacienteNombre : "Paciente particular", leftX, y);
        g.setFont(fSmall);
        y += 26;
        if (pacienteDni != null && !pacienteDni.isBlank()) {
            g.drawString("DNI: " + pacienteDni, leftX, y);
            y += 22;
        }
        if (pacienteTelefono != null && !pacienteTelefono.isBlank()) {
            g.drawString("Tel: " + pacienteTelefono, leftX, y);
            y += 22;
        }
        g.drawString("Motivo: " + recortar(motivo, 34), leftX, y);

        y += 46;
        g.setFont(new Font("DejaVu Sans", Font.BOLD, 17));
        g.drawString("Atendido por", leftX, y);
        y += 24;
        g.setFont(fSmall);
        g.drawString(nombreClinica, leftX, y);
        y += 20;
        g.drawString("Metodo de pago: Efectivo / Tarjeta", leftX, y);

        int termY = 700;
        int termW = leftColW - 20;
        int termH = 260;
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Float(leftX, termY, termW, termH, 8, 8));
        int tx = leftX + 22;
        int ty = termY + 40;
        g.setFont(new Font("DejaVu Sans", Font.BOLD, 18));
        g.drawString("Terminos y", tx, ty);
        ty += 24;
        g.drawString("condiciones", tx, ty);
        ty += 34;
        g.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 13));
        String[] lineasTerm = {
                "Este documento es un comprobante", "de atencion medica. El pago se",
                "realiza al momento de la consulta.", "Para conocer nuestros terminos y",
                "condiciones completos visita", SITIO_WEB
        };
        for (String linea : lineasTerm) {
            g.drawString(linea, tx, ty);
            ty += 20;
        }

        int tableX = leftX + leftColW + 30;
        int tableW = W - MARGIN - tableX;
        int rowH = 44;
        int headerY = 340;

        int colCant = tableX;
        int colCantW = 80;
        int colProd = colCant + colCantW;
        int colProdW = (int) (tableW * 0.42);
        int colPrecio = colProd + colProdW;
        int colPrecioW = (int) (tableW * 0.27);
        int colTotal = colPrecio + colPrecioW;
        int colTotalW = tableW - colCantW - colProdW - colPrecioW;

        g.setColor(AZUL);
        g.fillRect(colCant, headerY, tableW, rowH);
        g.setColor(FONDO);
        g.setFont(new Font("DejaVu Sans", Font.BOLD, 15));
        drawCentered(g, "Cant.", colCant, headerY, colCantW, rowH);
        drawCentered(g, "Servicio", colProd, headerY, colProdW, rowH);
        drawCentered(g, "Precio unit.", colPrecio, headerY, colPrecioW, rowH);
        drawCentered(g, "Total", colTotal, headerY, colTotalW, rowH);

        int rowY = headerY + rowH;
        g.setColor(AZUL);
        g.setFont(fSmall);
        for (ItemFactura it : items) {
            drawCentered(g, String.valueOf(it.cantidad), colCant, rowY, colCantW, rowH);
            g.drawString(recortar(it.producto, 34), colProd + 10, rowY + rowH / 2 + 6);
            drawRight(g, String.format(java.util.Locale.US, "S/ %.2f", it.precioUnitario), colPrecio, rowY, colPrecioW, rowH);
            drawRight(g, String.format(java.util.Locale.US, "S/ %.2f", it.getTotal()), colTotal, rowY, colTotalW, rowH);
            g.setStroke(new BasicStroke(1f));
            g.drawLine(colCant, rowY + rowH, colCant + tableW, rowY + rowH);
            rowY += rowH;
        }

        for (int i = 0; i < 5; i++) {
            g.drawLine(colCant, rowY + rowH, colCant + tableW, rowY + rowH);
            rowY += rowH;
        }


        int valueRight = W - MARGIN;
        Font fTotalBold = new Font("DejaVu Sans", Font.BOLD, 24);

        int totY = rowY + 46;
        drawParEtiquetaValor(g, "Subtotal:", fMono,
                String.format(java.util.Locale.US, "S/ %.2f", subtotal), fMono, valueRight, totY);
        totY += 28;
        drawParEtiquetaValor(g, "Impuestos (0%):", fMono,
                String.format(java.util.Locale.US, "S/ %.2f", impuestos), fMono, valueRight, totY);
        totY += 18;
        g.setColor(AZUL);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(valueRight - 260, totY, valueRight, totY);
        totY += 42;
        drawParEtiquetaValor(g, "Total:", fTotalBold,
                String.format(java.util.Locale.US, "S/ %.2f", total), fTotalBold, valueRight, totY);

        int footerY = H - 90;
        g.setStroke(new BasicStroke(2f));
        g.drawLine(MARGIN, footerY, W - MARGIN, footerY);
        g.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 14));
        g.drawString("Oficina en:  " + OFICINA_DIRECCION, MARGIN, footerY + 32);
        g.drawString("Contactanos: " + TELEFONO_CONTACTO + "  |  " + EMAIL_CONTACTO, MARGIN, footerY + 56);

        g.dispose();
        return img;
    }

    private static String obtenerIniciales(String nombreClinica) {
        String[] palabras = nombreClinica.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) sb.append(Character.toUpperCase(palabra.charAt(0)));
            if (sb.length() >= 3) break;
        }
        return sb.length() > 0 ? sb.toString() : "CL";
    }

    private static String recortar(String texto, int maxLen) {
        if (texto == null) return "";
        return texto.length() > maxLen ? texto.substring(0, maxLen - 1) + "\u2026" : texto;
    }

    private static void drawCentered(Graphics2D g, String texto, int x, int y, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(texto);
        g.drawString(texto, x + (w - tw) / 2f, y + h / 2f + fm.getAscent() / 2f - 3);
    }

    private static void drawRight(Graphics2D g, String texto, int x, int y, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(texto);
        g.drawString(texto, x + w - tw - 10, y + h / 2f + fm.getAscent() / 2f - 3);
    }


    private static void drawRightAt(Graphics2D g, String texto, int endX, int baselineY) {
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(texto);
        g.drawString(texto, endX - tw, baselineY);
    }

    private static void drawParEtiquetaValor(Graphics2D g, String etiqueta, Font fuenteEtiqueta,
                                             String valor, Font fuenteValor, int rightEdge, int baselineY) {
        g.setFont(fuenteValor);
        FontMetrics fmValor = g.getFontMetrics();
        int valorW = fmValor.stringWidth(valor);
        int valorX = rightEdge - valorW;
        g.drawString(valor, valorX, baselineY);

        g.setFont(fuenteEtiqueta);
        FontMetrics fmEtiqueta = g.getFontMetrics();
        int etiquetaW = fmEtiqueta.stringWidth(etiqueta);
        int gap = 18;
        int etiquetaX = valorX - gap - etiquetaW;
        g.drawString(etiqueta, etiquetaX, baselineY);
    }
}
