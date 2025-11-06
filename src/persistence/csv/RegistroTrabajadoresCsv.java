package persistence.csv;

import model.TrabajadorEsclavizado;
import persistence.CsvUtils;

import java.util.List;
import java.util.function.Function;

public final class RegistroTrabajadoresCsv {
    private RegistroTrabajadoresCsv() {}

    /** Cabecera del archivo CSV */
    public static String header() {
        return "registroId,trabajadorId";
    }

    /** Objeto -> fila CSV */
    public static String toRow(int registroId, TrabajadorEsclavizado t) {
        if (t == null) {
            throw new IllegalArgumentException("Trabajador no puede ser null al guardar registro.");
        }
        return registroId + "," + t.getId();
    }

    /** Fila CSV -> Trabajador (usa una función para buscarlo por id) */
    public static TrabajadorEsclavizado fromRow(String row, Function<Integer, TrabajadorEsclavizado> findById) {
        List<String> cols = CsvUtils.splitRow(row);

        if (cols.size() < 2) {
            throw new IllegalArgumentException("Línea inválida para RegistroTrabajadores: " + row);
        }

        int trabajadorId = parseIntSafe(cols.get(1));

        if (findById == null) {
            return null; // si no hay función de búsqueda
        }

        return findById.apply(trabajadorId);
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
