package persistence.csv;

import model.TrabajadorEsclavizado;
import persistence.CsvUtils;

import java.util.List;

public final class TrabajadorEsclavizadoCsv {
    private TrabajadorEsclavizadoCsv() {}

    public static String header() {
        return "id,nombre,paisOrigen,edad,fechaCaptura,salud,asignado";
    }

    public static String toRow(TrabajadorEsclavizado t) {
        return t.getId() + "," +
                CsvUtils.q(t.getNombre()) + "," +
                CsvUtils.q(t.getPaisOrigen()) + "," +
                t.getEdad() + "," +
                CsvUtils.q(t.getFechaCaptura()) + "," +
                CsvUtils.q(t.getSalud()) + "," +
                t.getAsignado();
    }

    public static TrabajadorEsclavizado fromRow(String row) {
        List<String> c = CsvUtils.splitRow(row);
        int id            = Integer.parseInt(c.get(0));
        String nombre     = c.get(1);
        String paisOrigen = c.get(2);
        int edad          = Integer.parseInt(c.get(3));
        String fechaCap   = c.get(4);
        String salud      = c.get(5);
        boolean asignado  = Boolean.parseBoolean(c.get(6));
        return new TrabajadorEsclavizado(id, nombre, paisOrigen, edad, fechaCap, salud, asignado);
    }
}
