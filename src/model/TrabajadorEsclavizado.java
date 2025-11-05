package model;

public class TrabajadorEsclavizado {
    private int id;
    private String nombre;
    private String paisOrigen;
    private int edad;
    private String fechaCaptura;
    private String salud;
    private boolean asignado;

    public TrabajadorEsclavizado(int id, String nombre, String paisOrigen, int edad, String fechaCaptura, String salud, boolean asignado) {
        this.id = id;
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.edad = edad;
        this.fechaCaptura = fechaCaptura;
        this.salud = salud;
        this.asignado = asignado;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPaisOrigen() { return paisOrigen; }
    public int getEdad() { return edad; }
    public String getFechaCaptura() { return fechaCaptura; }
    public String getSalud() { return salud; }
    public boolean getAsignado() { return asignado; } // usa get... para boolean para ser consistentes

}