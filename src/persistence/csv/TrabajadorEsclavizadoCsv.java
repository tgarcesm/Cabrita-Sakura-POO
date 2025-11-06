package persistence.csv;

import model.TrabajadorEsclavizado;
import persistence.CsvUtils;

import java.util.List;

public final class TrabajadorEsclavizadoCsv {
    private TrabajadorEsclavizadoCsv() {}

    /** Cabecera del CSV */
    public static String header() {
        return "id,nombre,paisOrigen,edad,fechaCaptura,salud,asignado";
    }

    /** Objeto -> fila CSV */
    public static String toRow(TrabajadorEsclavizado t) {
        return t.getId() + "," +
                CsvUtils.q(t.getNombre()) + "," +
                CsvUtils.q(t.getPaisOrigen()) + "," +
                t.getEdad() + "," +
                CsvUtils.q(t.getFechaCaptura()) + "," +
                CsvUtils.q(t.getSalud()) + "," +
                t.getAsignado();
    }

    /** Fila CSV -> objeto */
    public static TrabajadorEsclavizado fromRow(String row) {
        List<String> c = CsvUtils.splitRow(row);

        if (c.size() < 7) {
            throw new IllegalArgumentException("Línea inválida para TrabajadorEsclavizado: " + row);
        }

        int id            = parseIntSafe(c.get(0));
        String nombre     = c.get(1);
        String paisOrigen = c.get(2);
        int edad          = parseIntSafe(c.get(3));
        String fechaCap   = c.get(4);
        String salud      = c.get(5);
        boolean asignado  = parseBoolSafe(c.get(6));

        return new TrabajadorEsclavizado(id, nombre, paisOrigen, edad, fechaCap, salud, asignado);
    }

    /** Conversión segura de enteros */
    private static int parseIntSafe(String s) {
        try {
            return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Conversión segura de booleanos */
    private static boolean parseBoolSafe(String s) {
        if (s == null) return false;
        String val = s.trim().toLowerCase();
        return val.equals("true") || val.equals("1") || val.equals("sí") || val.equals("si");
    }
}
