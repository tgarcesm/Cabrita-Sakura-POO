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

    /** Objeto -> fila CSV */
    public static String toRow(Fabrica f) {
        return f.getId() + "," +
                CsvUtils.q(f.getPais()) + "," +
                CsvUtils.q(f.getCiudad()) + "," +
                f.getCapacidad() + "," +
                f.getNivelAutomatizacion();
    }

    /** Fila CSV -> objeto */
    public static Fabrica fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);
        int id = Integer.parseInt(cols.get(0));
        String pais = cols.get(1);
        String ciudad = cols.get(2);
        int capacidad = Integer.parseInt(cols.get(3));
        int nivelAutomatizacion = Integer.parseInt(cols.get(4));

        return new Fabrica(id, pais, ciudad, capacidad, nivelAutomatizacion);
    }
}
