package persistence.csv;

import model.MetodoPago;
import persistence.CsvUtils;

import java.util.List;

public final class MetodoPagoCsv {
    private MetodoPagoCsv() {}

    /** Cabecera del CSV */
    public static String header() {
        return "id,tipo,titular,numeroEnmascarado";
    }

    /** Objeto -> fila CSV (mismo orden que header) */
    public static String toRow(MetodoPago m) {
        return m.getId() + "," +
                CsvUtils.q(m.getTipo()) + "," +
                CsvUtils.q(m.getTitular()) + "," +
                CsvUtils.q(m.getNumeroEnmascarado());
    }

    /** Fila CSV -> objeto (mismo orden que header) */
    public static MetodoPago fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);

        if (cols.size() < 4) {
            throw new IllegalArgumentException("Línea inválida en metodo_pago.csv: " + row);
        }

        int id = parseIntSafe(cols.get(0));
        String tipo = cols.get(1);
        String titular = cols.get(2);
        String numeroEnmascarado = cols.get(3);

        return new MetodoPago(id, tipo, titular, numeroEnmascarado);
    }

    /** Maneja números vacíos o inválidos sin romper la carga */
    private static int parseIntSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
