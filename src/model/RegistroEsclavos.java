package model;

import java.util.ArrayList;

public class RegistroEsclavos {
    private String ultimoAcceso;
    private int nivelTrafico;
    private ArrayList<TrabajadorEsclavizado> trabajadores = new ArrayList<>();

    public void agregarEsclavo(TrabajadorEsclavizado t) { trabajadores.add(t); }
    public void eliminarEsclavo(TrabajadorEsclavizado t) { trabajadores.remove(t); }
}

