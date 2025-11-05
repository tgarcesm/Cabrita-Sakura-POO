package model;

public class MetodoPago {
    private int id;
    private String tipo;
    private String titular;
    private String numeroEnmascarado;

    public MetodoPago(int id, String tipo, String titular, String numeroEnmascarado) {
        this.id = id;
        this.tipo = tipo;
        this.titular = titular;
        this.numeroEnmascarado = numeroEnmascarado;


    }
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getTitular() { return titular; }
    public String getNumeroEnmascarado() { return numeroEnmascarado; }

}