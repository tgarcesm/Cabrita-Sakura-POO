package model;

import java.util.ArrayList;

public class Carrito {
    private int id;
    private String fechaCreacion;
    private ArrayList<LineaCarrito> lineas = new ArrayList<>();

    public Carrito(int id, String fechaCreacion) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        lineas.add(new LineaCarrito(producto, cantidad));
    }

    public void eliminarProducto(Producto producto) {
        lineas.removeIf(l -> l.getProducto().getId() == producto.getId());
    }

    public ArrayList<LineaCarrito> getLineas() { return lineas; }

    public int getId() { return id; }
    public String getFechaCreacion() { return fechaCreacion; }

}