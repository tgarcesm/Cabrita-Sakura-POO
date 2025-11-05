package persistence.csv;

import Exceptions.InvalidClientOperationException;
import model.Cliente;
import persistence.CsvUtils;

import java.util.List;

public final class ClienteCsv {
    private ClienteCsv() {}

    /** Cabecera estándar para el CSV de clientes */
    public static String header() {
        return "id,direccionEnvio,telefono";
    }

    /** Objeto -> fila CSV */
    public static String toRow(Cliente c) {
        return c.getId() + "," +
                CsvUtils.q(c.getDireccionEnvio()) + "," +
                CsvUtils.q(c.getTelefono());
    }

    /** Fila CSV -> objeto */
    public static Cliente fromRow(String row) throws InvalidClientOperationException {
        List<String> cols = CsvUtils.splitRow(row);
        int id             = Integer.parseInt(cols.get(0));
        String direccion   = cols.get(1);
        String telefono    = cols.get(2);

        // Constructor compatible con tu clase actual:
        return new Cliente(id, direccion, telefono);
    }
}
