package model;

public class AdministradorUsuario extends Usuario {
    private int nivelAcceso;

    public AdministradorUsuario(int id, String nombre, String email, String passwordHash, String rol, String fechaRegistro, boolean estadoCuenta, int nivelAcceso) {
        super(id, nombre, email, passwordHash, rol, fechaRegistro, estadoCuenta);
        this.nivelAcceso = nivelAcceso;
    }

    public void crearUsuario() { }
    public void suspenderUsuario() { }
    public void asignarUsuario() { }
    public void borrarUsuario() { }
}

