package persistence.csv;

import model.Carrito;
import persistence.CsvUtils;

import java.util.List;

public final class CarritoCsv {
    private CarritoCsv() {}

    /** Cabecera del CSV de carritos */
    public static String header() {
        return "id,fechaCreacion";
    }

    /** Objeto -> fila CSV (carrito) */
    public static String toRow(Carrito c) {
        return c.getId() + "," + CsvUtils.q(c.getFechaCreacion());
    }

    /** Fila CSV -> objeto (carrito) */
    public static Carrito fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);
        int id        = Integer.parseInt(cols.get(0));
        String fecha  = cols.get(1);
        return new Carrito(id, fecha);
    }
}
