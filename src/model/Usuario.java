package model;

public abstract class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol;
    private String fechaRegistro;
    private boolean estadoCuenta;

    public Usuario(int id, String nombre, String email, String passwordHash, String rol, String fechaRegistro, boolean estadoCuenta) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.estadoCuenta = estadoCuenta;
    }

    public void editarDatos(String nuevoNombre, String nuevoEmail) {
        this.nombre = nuevoNombre;
        this.email = nuevoEmail;
    }

    public boolean verificar(String password) {
        return this.passwordHash.equals(password);
    }

    public void resetearPassword(String nuevoHash) {
        this.passwordHash = nuevoHash;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }
    public String getFechaRegistro() { return fechaRegistro; }
    public boolean getEstadoCuenta() { return estadoCuenta; }
}