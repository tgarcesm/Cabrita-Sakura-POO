package persistence.csv;

import model.Fabrica;
import persistence.CsvUtils;

import java.util.List;

public final class FabricaCsv {
    private FabricaCsv() {}

    /** Cabecera del CSV */
    public static String header() {
        return "id,pais,ciudad,capacidad,nivelAutomatizacion";
    }

    /** Objeto -> fila CSV (mismo orden que header) */
    public static String toRow(Fabrica f) {
        return f.getId() + "," +
                CsvUtils.q(f.getPais()) + "," +
                CsvUtils.q(f.getCiudad()) + "," +
                f.getCapacidad() + "," +
                f.getNivelAutomatizacion();
    }

    /** Fila CSV -> objeto (mismo orden que header) */
    public static Fabrica fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);

        if (cols.size() < 5) {
            throw new IllegalArgumentException("Línea inválida para Fabrica: " + row);
        }

        int id = parseIntSafe(cols.get(0));
        String pais = cols.get(1);
        String ciudad = cols.get(2);
        int capacidad = parseIntSafe(cols.get(3));
        int nivelAutomatizacion = parseIntSafe(cols.get(4));

        return new Fabrica(id, pais, ciudad, capacidad, nivelAutomatizacion);
    }

    /** Maneja números vacíos o inválidos sin lanzar excepción grave */
    private static int parseIntSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
