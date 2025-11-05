package persistence.csv;

import model.Categoria;
import persistence.CsvUtils;

import java.util.List;

public final class CategoriaCsv {
    private CategoriaCsv() {}

    /** Cabecera del archivo CSV */
    public static String header() {
        return "id,nombre,descripcion";
    }

    /** Objeto -> fila CSV */
    public static String toRow(Categoria c) {
        return c.getId() + "," +
                CsvUtils.q(c.getNombre()) + "," +
                CsvUtils.q(c.getDescripcion());
    }

    /** Fila CSV -> objeto */
    public static Categoria fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);
        int id            = Integer.parseInt(cols.get(0));
        String nombre     = cols.get(1);
        String descripcion = cols.get(2);
        return new Categoria(id, nombre, descripcion);
    }
}
