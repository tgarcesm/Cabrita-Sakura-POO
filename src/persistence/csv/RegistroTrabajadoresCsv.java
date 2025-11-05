package persistence.csv;

import model.TrabajadorEsclavizado;
import java.util.List;
import java.util.function.Function;
import persistence.CsvUtils;

public final class RegistroTrabajadoresCsv {
    private RegistroTrabajadoresCsv() {}

    public static String header() {
        return "registroId,trabajadorId";
    }

    /** Objeto -> fila CSV */
    public static String toRow(int registroId, TrabajadorEsclavizado t) {
        return registroId + "," + t.getId();
    }

    /** Fila CSV -> Trabajador */
    public static TrabajadorEsclavizado fromRow(String row, Function<Integer, TrabajadorEsclavizado> findById) {
        List<String> cols = CsvUtils.splitRow(row);
        int trabajadorId = Integer.parseInt(cols.get(1));
        return findById.apply(trabajadorId);
    }
}
