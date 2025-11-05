package model;

public class AdministradorContenido extends Usuario {
    private boolean permisosEdicion;

    public AdministradorContenido(int id, String nombre, String email, String passwordHash, String rol, String fechaRegistro, boolean estadoCuenta) {
        super(id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta);
        this.permisosEdicion = true;
    }

    public void crearProducto() { }
    public void editarProducto() { }
    public void publicarProducto() { }
    public void borrarProducto() { }

    public boolean getPermisosEdicion() {
        return permisosEdicion;
    }
    public void setPermisosEdicion(boolean permisosEdicion) {
        this.permisosEdicion = permisosEdicion;
    }

}