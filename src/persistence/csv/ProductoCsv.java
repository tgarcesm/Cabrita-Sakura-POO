package persistence.csv;

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
        int categoriaId = (p.getCategoria() != null) ? p.getCategoria().getId() : 0;
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
     * @param findCategoriaById función que devuelve una Categoria por su id
     */
    public static Producto fromRow(String row, Function<Integer, Categoria> findCategoriaById)
            throws Exceptions.InvalidProductException {

        List<String> c = CsvUtils.splitRow(row);

        int id               = Integer.parseInt(c.get(0));
        String nombre        = c.get(1);
        String descripcion   = c.get(2);
        double precio        = Double.parseDouble(c.get(3));
        int stock            = Integer.parseInt(c.get(4));
        String fecha         = c.get(5);

        Categoria categoria = null;
        if (c.size() > 6 && !c.get(6).isBlank()) {
            int categoriaId = Integer.parseInt(c.get(6));
            categoria = findCategoriaById.apply(categoriaId);
        }

        // Creamos el producto
        Producto p = new Producto(id, nombre, descripcion, precio, stock, fecha, categoria);

        return p;
    }
}
