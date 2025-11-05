package persistence.csv;

import model.Usuario;
import java.util.List;
import java.util.function.Function;
import persistence.CsvUtils;

public final class ConsejoMiembrosCsv {
    private ConsejoMiembrosCsv() {}

    /** Cabecera del archivo que relaciona ConsejoSombrio con sus miembros */
    public static String header() {
        return "consejoId,usuarioId";
    }

    /** Relación -> fila CSV */
    public static String toRow(int consejoId, Usuario usuario) {
        return consejoId + "," + usuario.getId();
    }

    /** Fila CSV -> Usuario */
    public static Usuario fromRow(String row, Function<Integer, Usuario> findUsuarioById) {
        List<String> cols = CsvUtils.splitRow(row);
        // c0 = consejoId, c1 = usuarioId
        int usuarioId = Integer.parseInt(cols.get(1));
        return findUsuarioById.apply(usuarioId);
    }
}
