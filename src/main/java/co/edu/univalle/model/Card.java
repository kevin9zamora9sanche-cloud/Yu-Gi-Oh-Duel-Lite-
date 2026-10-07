package co.edu.univalle.model;

import java.awt.*;

public class Card {
    private String nombre;
    private int atk;
    private int def;
    private Image imagen;

    public Card(String nombre, int atk, int def, Image imagen) {
        this.nombre = nombre;
        this.atk = atk;
        this.def = def;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public Image getImagen() {
        return imagen;
    }

    public void setImagen(Image imagen) {
        this.imagen = imagen;
    }
}
