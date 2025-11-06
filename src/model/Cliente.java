package model;

import Exceptions.InvalidClientOperationException;

import java.util.ArrayList;

public class Cliente extends Usuario {

    private String direccionEnvio;
    private String telefono;
    private ArrayList<Compra> compras = new ArrayList<>();

    // Constructor completo (Cliente hereda de Usuario)
    public Cliente(int id,
                   String nombre,
                   String email,
                   String passwordHash,
                   String rol,
                   String fechaRegistro,
                   boolean estadoCuenta,
                   String direccionEnvio,
                   String telefono) throws InvalidClientOperationException {
        super(id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta);
        if (telefono == null || telefono.isEmpty())
            throw new InvalidClientOperationException("El número de teléfono no puede estar vacío.");
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
    }


    // --- negocio compras ---
    public void agregarCompra(Compra compra) throws InvalidClientOperationException {
        if (compra == null) throw new InvalidClientOperationException("No se puede agregar una compra nula.");
        compras.add(compra);
    }

    // --- getters / setters ---
    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public ArrayList<Compra> getCompras() { return compras; }
}
