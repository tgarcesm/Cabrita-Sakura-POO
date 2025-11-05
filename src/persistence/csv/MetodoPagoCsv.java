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

    /** Objeto -> fila CSV */
    public static String toRow(MetodoPago m) {
        return m.getId() + "," +
                CsvUtils.q(m.getTipo()) + "," +
                CsvUtils.q(m.getTitular()) + "," +
                CsvUtils.q(m.getNumeroEnmascarado());
    }

    /** Fila CSV -> objeto */
    public static MetodoPago fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);
        int id = Integer.parseInt(cols.get(0));
        String tipo = cols.get(1);
        String titular = cols.get(2);
        String numeroEnmascarado = cols.get(3);
        return new MetodoPago(id, tipo, titular, numeroEnmascarado);
    }
}
