package model;

import java.util.ArrayList;

public class Fabrica {
    private static int contador = 1;
    private int id;
    private String pais;
    private String ciudad;
    private int capacidad;
    private int nivelAutomatizacion;
    private ArrayList<TrabajadorEsclavizado> trabajadores = new ArrayList<>();

    public Fabrica(int id, String pais, String ciudad, int capacidad, int nivelAutomatizacion) {
        this.id = id;
        this.pais = pais;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
        this.nivelAutomatizacion = nivelAutomatizacion;
    }

    public void asignarTrabajador(TrabajadorEsclavizado t) {
        trabajadores.add(t);
    }

    public int getId() { return id; }
    public String getPais() { return pais; }
    public String getCiudad() { return ciudad; }

    public int getCapacidad() { return capacidad; }
    public int getNivelAutomatizacion() { return nivelAutomatizacion; }

}