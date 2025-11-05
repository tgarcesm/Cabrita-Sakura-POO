package persistence.csv;

import model.LineaCarrito;
import model.Producto;
import persistence.CsvUtils;

import java.util.List;
import java.util.function.Function;

public final class LineaCarritoCsv {
    private LineaCarritoCsv() {}

    /** Cabecera del CSV para las líneas de carrito */
    public static String header() {
        return "carritoId,productoId,cantidad";
    }

    /**
     * Convierte una línea de carrito a una fila CSV.
     * @param l la línea de carrito
     * @param carritoId el id del carrito al que pertenece
     */
    public static String toRow(LineaCarrito l, int carritoId) {
        int productoId = l.getProducto().getId();
        return carritoId + "," + productoId + "," + l.getCantidad();
    }

    /**
     * Reconstruye una línea de carrito desde una fila CSV.
     * @param row la fila
     * @param findProductoById función que recibe un id y devuelve un Producto
     * @return nueva LineaCarrito
     */
    public static LineaCarrito fromRow(String row, Function<Integer, Producto> findProductoById) {
        List<String> c = CsvUtils.splitRow(row);

        // c[0] = carritoId (lo usa quien llama para asociar)
        int productoId = Integer.parseInt(c.get(1));
        int cantidad   = Integer.parseInt(c.get(2));

        Producto p = findProductoById.apply(productoId);
        return new LineaCarrito(p, cantidad);
    }
}
