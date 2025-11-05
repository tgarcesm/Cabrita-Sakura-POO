package model;

import Exceptions.InvalidClientOperationException;

import java.util.ArrayList;

public class Cliente {
    private static int contador = 1;
    private int id;
    private String direccionEnvio;
    private String telefono;
    private ArrayList<Compra> compras = new ArrayList<>();

    public Cliente(int i, String direccionEnvio, String telefono) throws InvalidClientOperationException {
        if (telefono == null || telefono.isEmpty()) throw new InvalidClientOperationException("El número de teléfono no puede estar vacío.");
        this.id = contador++;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
    }

    public void agregarCompra(Compra compra) throws InvalidClientOperationException {
        if (compra == null) throw new InvalidClientOperationException("No se puede agregar una compra nula.");
        compras.add(compra);
    }

    public int getId() { return id; }
    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public ArrayList<Compra> getCompras() { return compras; }
}