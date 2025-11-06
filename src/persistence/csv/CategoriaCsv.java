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

    /** Objeto -> fila CSV (mismo orden que header) */
    public static String toRow(Categoria c) {
        return c.getId() + "," +
                CsvUtils.q(c.getNombre()) + "," +
                CsvUtils.q(c.getDescripcion());
    }

    /** Fila CSV -> objeto (mismo orden que header) */
    public static Categoria fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);

        // Robustez básica por si hay líneas incompletas
        if (cols.size() < 3) {
            // Puedes lanzar una excepción específica si prefieres:
            // throw new IllegalArgumentException("Línea inválida para Categoria: " + row);
            // o devolver una categoría mínima:
            int idFallback = (cols.size() > 0) ? Integer.parseInt(cols.get(0)) : 0;
            String nombreFallback = (cols.size() > 1) ? cols.get(1) : "";
            String descFallback = (cols.size() > 2) ? cols.get(2) : "";
            return new Categoria(idFallback, nombreFallback, descFallback);
        }

        int id          = Integer.parseInt(cols.get(0));
        String nombre   = cols.get(1);
        String desc     = cols.get(2);
        return new Categoria(id, nombre, desc);
    }
}
