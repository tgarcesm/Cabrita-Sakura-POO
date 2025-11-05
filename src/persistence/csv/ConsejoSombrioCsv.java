package persistence.csv;

import model.ConsejoSombrio;
import persistence.CsvUtils;

import java.util.List;

public final class ConsejoSombrioCsv {
    private ConsejoSombrioCsv() {}

    /** Cabecera principal del archivo CSV */
    public static String header() {
        return "id,nombreClave";
    }

    /** Objeto -> fila CSV */
    public static String toRow(ConsejoSombrio c) {
        return c.getId() + "," + CsvUtils.q(c.getNombreClave());
    }

    /** Fila CSV -> objeto */
    public static ConsejoSombrio fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);
        int id = Integer.parseInt(cols.get(0));
        String nombreClave = cols.get(1);

        ConsejoSombrio consejo = new ConsejoSombrio();
        try {
            // Accedemos por reflexión a los campos privados,
            // o puedes crear un constructor (int id, String nombreClave)
            var fId = ConsejoSombrio.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.setInt(consejo, id);

            var fNombre = ConsejoSombrio.class.getDeclaredField("nombreClave");
            fNombre.setAccessible(true);
            fNombre.set(consejo, nombreClave);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return consejo;
    }
}
