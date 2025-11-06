package persistence.csv;

import model.AdministradorUsuario;
import persistence.CsvUtils;

import java.util.List;

public final class AdministradorUsuarioCsv {
    private AdministradorUsuarioCsv() {}

    /** Cabecera del CSV */
    public static String header() {
        return "id,nombre,email,passwordHash,rol,fechaRegistro,estadoCuenta,nivelAcceso";
    }

    /** Objeto -> fila CSV */
    public static String toRow(AdministradorUsuario a) {
        return a.getId() + "," +
                CsvUtils.q(a.getNombre()) + "," +
                CsvUtils.q(a.getEmail()) + "," +
                CsvUtils.q(a.getPasswordHash()) + "," +
                CsvUtils.q(a.getRol()) + "," +
                CsvUtils.q(a.getFechaRegistro()) + "," +
                a.getEstadoCuenta() + "," +
                a.getNivelAcceso();
    }

    /** Fila CSV -> objeto */
    public static AdministradorUsuario fromRow(String row) {
        List<String> c = CsvUtils.splitRow(row);
        int id               = Integer.parseInt(c.get(0));
        String nombre        = c.get(1);
        String email         = c.get(2);
        String passwordHash  = c.get(3);
        String rol           = c.get(4);
        String fechaRegistro = c.get(5);
        boolean estadoCuenta = Boolean.parseBoolean(c.get(6));
        int nivelAcceso      = Integer.parseInt(c.get(7));

        return new AdministradorUsuario(
                id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta, nivelAcceso
        );
    }
}
