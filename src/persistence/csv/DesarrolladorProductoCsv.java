package persistence.csv;

import model.DesarrolladorProducto;
import persistence.CsvUtils;

import java.util.List;

public final class DesarrolladorProductoCsv {
    private DesarrolladorProductoCsv() {}

    /** Cabecera del CSV */
    public static String header() {
        return "id,nombre,email,passwordHash,rol,fechaRegistro,estadoCuenta,permisoEdicion";
    }

    /** Objeto -> fila CSV */
    public static String toRow(DesarrolladorProducto d) {
        return d.getId() + "," +
                CsvUtils.q(d.getNombre()) + "," +
                CsvUtils.q(d.getEmail()) + "," +
                CsvUtils.q(d.getPasswordHash()) + "," +
                CsvUtils.q(d.getRol()) + "," +
                CsvUtils.q(d.getFechaRegistro()) + "," +
                d.getEstadoCuenta() + "," +
                d.getPermisoEdicion();
    }

    /** Fila CSV -> objeto */
    public static DesarrolladorProducto fromRow(String row) {
        List<String> cols = CsvUtils.splitRow(row);

        int id               = Integer.parseInt(cols.get(0));
        String nombre        = cols.get(1);
        String email         = cols.get(2);
        String passwordHash  = cols.get(3);
        String rol           = cols.get(4);
        String fechaRegistro = cols.get(5);
        boolean estadoCuenta = Boolean.parseBoolean(cols.get(6));
        boolean permisoEdicion = Boolean.parseBoolean(cols.get(7));

        DesarrolladorProducto d = new DesarrolladorProducto(
                id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta
        );
        d.setPermisoEdicion(permisoEdicion);
        return d;
    }
}
