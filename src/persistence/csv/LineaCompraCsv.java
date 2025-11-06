package persistence.csv;

import model.LineaCompra;
import model.Producto;
import persistence.CsvUtils;

import java.util.List;
import java.util.function.Function;

public final class LineaCompraCsv {
    private LineaCompraCsv() {}

    /** Cabecera del CSV de líneas de compra */
    public static String header() {
        return "compraId,productoId,cantidad";
    }

    /**
     * Objeto -> fila CSV.
     * @param l         la línea de compra
     * @param compraId  id de la compra a la que pertenece
     */
    public static String toRow(LineaCompra l, int compraId) {
        int productoId = (l.getProducto() != null) ? l.getProducto().getId() : 0;
        return compraId + "," + productoId + "," + l.getCantidad();
    }

    /**
     * Fila CSV -> objeto (línea).
     * Recibe un lookup para reconstruir el Producto por id.
     * La asociación con la Compra (por compraId) la hace quien llama.
     */
    public static LineaCompra fromRow(String row, Function<Integer, Producto> findProductoById) {
        List<String> cols = CsvUtils.splitRow(row);

        // Robustez básica
        if (cols.size() < 3) {
            // fallback básico o lanza excepción si quieres depurar
            throw new IllegalArgumentException("Línea de LineaCompra inválida: " + row);
        }

        int productoId = Integer.parseInt(cols.get(1));
        int cantidad   = Integer.parseInt(cols.get(2));

        Producto p = null;
        if (findProductoById != null) {
            p = findProductoById.apply(productoId);
        }

        return new LineaCompra(p, cantidad);
    }
}
