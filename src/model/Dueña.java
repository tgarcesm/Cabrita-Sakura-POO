package model;

public class Dueña extends Usuario {
    private String claveMaestra;
    private String fechaCoronacion;

    public Dueña(int id, String nombre, String email, String passwordHash, String rol, String fechaRegistro, boolean estadoCuenta, String claveMaestra, String fechaCoronacion) {
        super(id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta);
        this.claveMaestra = claveMaestra;
        this.fechaCoronacion = fechaCoronacion;
    }

    public void accesoCompleto() { }
    public void controlarGestion() { }
    public void designarRoles() { }
    public void concederPermisos() { }
}
