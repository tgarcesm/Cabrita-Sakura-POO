package model;

public class DesarrolladorProducto extends Usuario {
    private boolean permisoEdicion;

    public DesarrolladorProducto(int id, String nombre, String email, String passwordHash, String rol, String fechaRegistro, boolean estadoCuenta) {
        super(id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta);
        this.permisoEdicion = true;
    }

    public void desarrollarProducto() { }

    public boolean getPermisoEdicion() {
        return permisoEdicion;
    }



    public void setPermisoEdicion(boolean permisoEdicion) {
        this.permisoEdicion = permisoEdicion;
    }

}