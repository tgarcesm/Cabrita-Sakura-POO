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

        if (cols.size() < 3) {
            throw new IllegalArgumentException("Línea inválida para RegistroEsclavos: " + row);
        }

        int id = parseIntSafe(cols.get(0));
        String ultimoAcceso = cols.get(1);
        int nivelTrafico = parseIntSafe(cols.get(2));

        RegistroEsclavos r = new RegistroEsclavos();

        try {
            // Asignar campos mediante reflexión (si no existen setters públicos)
            var fId = RegistroEsclavos.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.setInt(r, id);

            var fUlt = RegistroEsclavos.class.getDeclaredField("ultimoAcceso");
            fUlt.setAccessible(true);
            fUlt.set(r, ultimoAcceso);

            var fNiv = RegistroEsclavos.class.getDeclaredField("nivelTrafico");
            fNiv.setAccessible(true);
            fNiv.setInt(r, nivelTrafico);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return r;
    }

    /** Manejo seguro de enteros vacíos o inválidos */
    private static int parseIntSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
