package persistence.csv;

import model.AdministradorContenido;
import persistence.CsvUtils;
import java.util.List;

public final class AdministradorContenidoCsv {
    private AdministradorContenidoCsv() {}

    /** Cabecera del CSV (orden estable) */
    public static String header() {
        return "id,nombre,email,passwordHash,rol,fechaRegistro,estadoCuenta,permisosEdicion";
    }

    /** Objeto -> fila CSV (mismo orden que header) */
    public static String toRow(AdministradorContenido a) {
        return a.getId() + "," +
                CsvUtils.q(a.getNombre()) + "," +
                CsvUtils.q(a.getEmail()) + "," +
                CsvUtils.q(a.getPasswordHash()) + "," +
                CsvUtils.q(a.getRol()) + "," +
                CsvUtils.q(a.getFechaRegistro()) + "," +
                a.getEstadoCuenta() + "," +
                a.getPermisosEdicion();
    }

    /** Fila CSV -> objeto (mismo orden que header) */
    public static AdministradorContenido fromRow(String row) {
        List<String> c = CsvUtils.splitRow(row);
        int id               = Integer.parseInt(c.get(0));
        String nombre        = c.get(1);
        String email         = c.get(2);
        String passwordHash  = c.get(3);
        String rol           = c.get(4);
        String fechaRegistro = c.get(5);
        boolean estadoCuenta = Boolean.parseBoolean(c.get(6));
        boolean permisos     = Boolean.parseBoolean(c.get(7));

        AdministradorContenido a = new AdministradorContenido(
                id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta
        );
        a.setPermisosEdicion(permisos);
        return a;
    }
}
