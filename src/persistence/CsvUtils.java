package persistence;

import java.util.ArrayList;
import java.util.List;

public final class CsvUtils {
    private CsvUtils() {}

    // Escapa comillas y envuelve en ""
    public static String q(String s) {
        if (s == null) return "\"\"";
        String t = s.replace("\"", "\"\"");
        return "\"" + t + "\"";
    }

    // Divide una fila CSV con comillas
    public static List<String> splitRow(String row) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                if (i + 1 < row.length() && row.charAt(i + 1) == '"') { sb.append('"'); i++; }
            } else if (c == ',' && !inQuotes) {
                out.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        out.add(sb.toString());
        return out;
    }
}
