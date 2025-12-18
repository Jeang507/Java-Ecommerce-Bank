package util;

import model.Producto;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FacturaTXTGenerator {

    // Genera un archivo TXT con la factura de compra
    public static String generarFactura(
            String usuario,
            List<Producto> productos,
            double total
    ) {

        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String fileName = "factura_" + usuario + "_" + timestamp + ".txt";

            PrintWriter out = new PrintWriter(new FileWriter(fileName));

            out.println("========================================");
            out.println("              YULIANA APP");
            out.println("           FACTURA DE COMPRA");
            out.println("========================================");
            out.println();

            out.println("Cliente: " + usuario);
            out.println("Fecha: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            out.println("----------------------------------------");

            out.println("PRODUCTO                       PRECIO");
            out.println("----------------------------------------");

            for (Producto p : productos) {
                out.printf("%-30s $%.2f%n",
                        p.getNombre(),
                        p.getPrecio()
                );
            }

            out.println("----------------------------------------");
            out.printf("TOTAL A PAGAR:                 $%.2f%n", total);
            out.println("----------------------------------------");
            out.println();

            out.println("Gracias por su compra.");
            out.println("YulianaApp - La tienda perfecta para tu bebé.");
            out.println("========================================");

            out.close();
            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
