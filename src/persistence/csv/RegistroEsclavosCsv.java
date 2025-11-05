package persistence.csv;

import model.RegistroEsclavos;
import persistence.CsvUtils;

import java.util.List;

public final class RegistroEsclavosCsv {
    private RegistroEsclavosCsv() {}

    /** Cabecera del CSV principal */
    public static String header() {
        return "id,ultimoAcceso,nivelTrafico";
    }

    /** Objeto -> fila CSV */
    public static String toRow(RegistroEsclavos r, int id) {
        return id + "," +
                CsvUtils.q(r.getUltimoAcceso()) + "," +
                r.getNivelTrafico();
    }

    /** Fila CSV -> objeto */
    public static RegistroEsclavos fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);
        String ultimoAcceso = cols.get(1);
        int nivelTrafico = Integer.parseInt(cols.get(2));

        RegistroEsclavos r = new RegistroEsclavos();
        try {
            var f1 = RegistroEsclavos.class.getDeclaredField("ultimoAcceso");
            f1.setAccessible(true);
            f1.set(r, ultimoAcceso);

            var f2 = RegistroEsclavos.class.getDeclaredField("nivelTrafico");
            f2.setAccessible(true);
            f2.setInt(r, nivelTrafico);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return r;
    }
}
