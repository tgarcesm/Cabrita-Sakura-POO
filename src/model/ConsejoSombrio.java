package model;

import java.util.ArrayList;

public class ConsejoSombrio {
    private int id;
    private String nombreClave;
    private ArrayList<Usuario> miembros = new ArrayList<>();

    public ConsejoSombrio(int id, String nombreClave) {
    }

    public void agregarMiembro(Usuario u) { miembros.add(u); }
    public void removerMiembro(Usuario u) { miembros.remove(u); }
    public void verMiembros() {
        for (Usuario u : miembros) System.out.println(u.getNombre() + " - " + u.getRol());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }
    public void setNombreClave(String nombreClave) { this.nombreClave = nombreClave; }


    public String getNombreClave() {
        return nombreClave;
    }

    public ArrayList<Usuario> getMiembros() {
        return miembros;
    }
}