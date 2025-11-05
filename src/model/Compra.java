package model;

import Exceptions.EmptyCartException;
import java.util.ArrayList;

public class Compra {
    private int id;
    private String fecha;
    private double total;
    private String estado;
    private MetodoPago metodoPago;
    private ArrayList<LineaCompra> lineas = new ArrayList<>();

    // ===== CONSTRUCTORES =====

    // Constructor vacío
    public Compra() {
        this.fecha = "2025";

    }

    // Constructor completo (usado al cargar desde CSV)
    public Compra(int id, String fecha, double total, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    // ===== MÉTODOS DE NEGOCIO =====

    public void setLineasDesdeCarrito(ArrayList<LineaCarrito> lineasCarrito) throws EmptyCartException {
        if (lineasCarrito == null || lineasCarrito.isEmpty()) {
            throw new EmptyCartException("El carrito está vacío, no se puede generar compra.");
        }

        lineas.clear();
        for (LineaCarrito lc : lineasCarrito) {
            lineas.add(new LineaCompra(lc.getProducto(), lc.getCantidad()));
        }
    }

    public void calcularTotal() {
        total = 0;
        for (LineaCompra l : lineas) {
            total += l.getSubtotal();
        }
    }

    // ===== GETTERS Y SETTERS =====

    public int getId() {
        return id;
    }

    public void setId(int id) {  // necesario para carga desde CSV
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {  // necesario para carga desde CSV
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {  // necesario para carga desde CSV
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public ArrayList<LineaCompra> getLineas() {
        return lineas;
    }
}