package persistence.csv;

import model.ConsejoSombrio;
import persistence.CsvUtils;

import java.util.List;

public final class ConsejoSombrioCsv {
    private ConsejoSombrioCsv() {}

    public static String header() {
        return "id,nombreClave";
    }

    public static String toRow(ConsejoSombrio c) {
        return c.getId() + "," + CsvUtils.q(c.getNombreClave());
    }

    public static ConsejoSombrio fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);
        if (cols.size() < 2) {
            throw new IllegalArgumentException("Línea inválida para ConsejoSombrio: " + row);
        }
        int id = Integer.parseInt(cols.get(0));
        String nombreClave = cols.get(1);

        // Opción A: constructor
        return new ConsejoSombrio(id, nombreClave);

        // Opción B (si no hay constructor):
        // ConsejoSombrio c = new ConsejoSombrio();
        // c.setId(id);
        // c.setNombreClave(nombreClave);
        // return c;
    }
}
