package co.edu.univalle.model;

import java.awt.*;

public class Card {
    private String nombre;
    private int atk;
    private int def;
    private String imageUrl;
    private String type;

    public Card(String nombre, int atk, int def, String imageUrl, String type) {
        this.nombre = nombre;
        this.atk = atk;
        this.def = def;
        this.imageUrl = imageUrl;
        this.type = type;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*
        Revisamos que contenta el tipo "Monster" ya que pueden haber varios tipos de Mounstros
     */
    public boolean isMonster() {
        return type != null && type.toLowerCase().contains("monster");
    }
}
