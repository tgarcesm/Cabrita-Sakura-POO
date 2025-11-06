package persistence.csv;

import model.Usuario;
import persistence.CsvUtils;

import java.util.List;
import java.util.function.Function;

public final class ConsejoMiembrosCsv {
    private ConsejoMiembrosCsv() {}

    /** Cabecera del archivo que relaciona ConsejoSombrio con sus miembros */
    public static String header() {
        return "consejoId,usuarioId";
    }

    /** Relación -> fila CSV */
    public static String toRow(int consejoId, Usuario usuario) {
        int usuarioId = (usuario != null) ? usuario.getId() : 0;
        return consejoId + "," + usuarioId;
    }

    /** Fila CSV -> Usuario (requiere función de búsqueda por id) */
    public static Usuario fromRow(String row, Function<Integer, Usuario> findUsuarioById) {
        List<String> cols = CsvUtils.splitRow(row);

        // Validación básica
        if (cols.size() < 2) {
            throw new IllegalArgumentException("Línea inválida en consejo_miembros.csv: " + row);
        }

        int usuarioId = Integer.parseInt(cols.get(1));

        if (findUsuarioById == null) {
            return null; // evita NullPointer si no se pasó función
        }

        return findUsuarioById.apply(usuarioId);
    }
}
