package model;


import Exceptions.InvalidProductException;

public class Producto {
    private static int contador = 1;
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String fechaLanzamiento;
    private Categoria categoria;

    public Producto(int i, String nombre, String descripcion, double precio, int stock, String fechaLanzamiento, Categoria categoria)
            throws InvalidProductException {

        if (nombre == null || nombre.isEmpty()) throw new InvalidProductException("El nombre no puede estar vacío.");
        if (precio < 0) throw new InvalidProductException("El precio no puede ser negativo.");
        if (stock < 0) throw new InvalidProductException("El stock no puede ser negativo.");

        this.id = contador++;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.fechaLanzamiento = fechaLanzamiento;
        this.categoria = categoria;
    }

    public void setPrecio(double nuevoPrecio) throws InvalidProductException {
        if (nuevoPrecio < 0) throw new InvalidProductException("El precio no puede ser negativo.");
        this.precio = nuevoPrecio;
    }

    public void setStock(int nuevoStock) throws InvalidProductException {
        if (nuevoStock < 0) throw new InvalidProductException("El stock no puede ser negativo.");
        this.stock = nuevoStock;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public Categoria getCategoria() { return categoria; }
    public String getFechaLanzamiento() { return fechaLanzamiento;}


    @Override
    public String toString() {
        return id + " - " + nombre + " ($" + precio + ") - Stock: " + stock + " | Cat: " + categoria.getNombre();
    }


}