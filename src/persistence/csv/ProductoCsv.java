package persistence.csv;

import Exceptions.InvalidProductException;  // <-- importante para cuadrar con DataManager
import model.Producto;
import model.Categoria;
import persistence.CsvUtils;

import java.util.List;
import java.util.function.Function;

public final class ProductoCsv {
    private ProductoCsv() {}

    /** Cabecera del CSV */
    public static String header() {
        return "id,nombre,descripcion,precio,stock,fechaLanzamiento,categoriaId";
    }

    /** Objeto -> fila CSV */
    public static String toRow(Producto p) {
        // Guardar vacío cuando no hay categoría para no confundir con id=0 real
        String categoriaId = (p.getCategoria() != null) ? String.valueOf(p.getCategoria().getId()) : "";
        return p.getId() + "," +
                CsvUtils.q(p.getNombre()) + "," +
                CsvUtils.q(p.getDescripcion()) + "," +
                p.getPrecio() + "," +
                p.getStock() + "," +
                CsvUtils.q(p.getFechaLanzamiento()) + "," +
                categoriaId;
    }

    /**
     * Fila CSV -> objeto Producto
     * @param row la línea CSV
     * @param findCategoriaById función que devuelve una Categoria por su id (puede ser null)
     */
    public static Producto fromRow(String row, Function<Integer, Categoria> findCategoriaById)
            throws InvalidProductException {

        List<String> c = CsvUtils.splitRow(row);

        // Validación mínima: 6 campos obligatorios (el 7° categoriaId puede faltar o venir vacío)
        if (c.size() < 6) {
            throw new IllegalArgumentException("Línea inválida para Producto: " + row);
        }

        int id               = parseIntSafe(c.get(0));
        String nombre        = c.get(1);
        String descripcion   = c.get(2);
        double precio        = parseDoubleSafe(c.get(3));
        int stock            = parseIntSafe(c.get(4));
        String fecha         = c.get(5);

        Categoria categoria = null;
        if (c.size() > 6 && c.get(6) != null && !c.get(6).isBlank()) {
            int categoriaId = parseIntSafe(c.get(6));
            if (findCategoriaById != null) {
                categoria = findCategoriaById.apply(categoriaId);
                // Si no la encuentra, dejamos categoria=null (no rompemos la carga)
            }
        }

        // Crea el producto con o sin categoría (según tu modelo)
        return new Producto(id, nombre, descripcion, precio, stock, fecha, categoria);
    }

    // Utilidades de parseo seguro (evitan NumberFormatException)
    private static int parseIntSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static double parseDoubleSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? 0.0 : Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
