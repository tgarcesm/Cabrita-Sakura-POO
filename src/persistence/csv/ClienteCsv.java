package persistence.csv;

import Exceptions.InvalidClientOperationException;
import model.Cliente;
import persistence.CsvUtils;

import java.util.List;

public final class ClienteCsv {

    private ClienteCsv() {}

    /** Cabecera del CSV (orden estable, SIN salto de línea al final) */
    public static String header() {
        return "id,nombre,email,passwordHash,rol,fechaRegistro,estadoCuenta,direccionEnvio,telefono";
    }

    /** Objeto -> fila CSV (mismo orden que header) */
    public static String toRow(Cliente c) {
        return c.getId() + "," +
                CsvUtils.q(c.getNombre()) + "," +
                CsvUtils.q(c.getEmail()) + "," +
                CsvUtils.q(c.getPasswordHash()) + "," +
                CsvUtils.q(c.getRol()) + "," +
                CsvUtils.q(c.getFechaRegistro()) + "," +
                c.getEstadoCuenta() + "," +
                CsvUtils.q(c.getDireccionEnvio()) + "," +
                CsvUtils.q(c.getTelefono());
    }

    /** Fila CSV -> objeto (mismo orden que header) */
    public static Cliente fromRow(String row) throws InvalidClientOperationException {
        List<String> c = CsvUtils.splitRow(row);

        int id               = Integer.parseInt(c.get(0));
        String nombre        = c.get(1);
        String email         = c.get(2);
        String passwordHash  = c.get(3);
        String rol           = c.get(4);
        String fechaRegistro = c.get(5);
        boolean estadoCuenta = Boolean.parseBoolean(c.get(6));
        String direccion     = c.get(7);
        String telefono      = c.get(8);

        return new Cliente(
                id,
                nombre,
                email,
                passwordHash,
                rol,
                fechaRegistro,
                estadoCuenta,
                direccion,
                telefono
        );
    }
}
