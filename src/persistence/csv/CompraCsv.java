package persistence.csv;

import model.Compra;
import model.MetodoPago;
import persistence.CsvUtils;

import java.util.List;
import java.util.function.Function;

public final class CompraCsv {
    private CompraCsv() {}

    /** Cabecera del archivo CSV */
    public static String header() {
        return "id,fecha,total,estado,metodoPagoId";
    }

    /** Objeto -> fila CSV */
    public static String toRow(Compra c) {
        // Si no hay método de pago, se guarda vacío
        String metodoId = (c.getMetodoPago() != null) ? String.valueOf(c.getMetodoPago().getId()) : "";
        return c.getId() + "," +
                CsvUtils.q(c.getFecha()) + "," +
                c.getTotal() + "," +
                CsvUtils.q(c.getEstado()) + "," +
                metodoId;
    }

    /** Fila CSV -> objeto (usa un buscador para reconstruir el método de pago) */
    public static Compra fromRow(String row, Function<Integer, MetodoPago> findMetodoPagoById) {
        List<String> cols = CsvUtils.splitRow(row);
        int id             = Integer.parseInt(cols.get(0));
        String fecha       = cols.get(1);
        double total       = Double.parseDouble(cols.get(2));
        String estado      = cols.get(3);

        MetodoPago metodo = null;
        if (cols.size() > 4 && !cols.get(4).isBlank()) {
            int metodoId = Integer.parseInt(cols.get(4));
            metodo = findMetodoPagoById.apply(metodoId);
        }

        Compra c = new Compra(id, fecha, total, estado);
        c.setMetodoPago(metodo);
        return c;
    }
}
