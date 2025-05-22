package domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Sala {

    @Id
    private String nombre;
    private int aforo;

    public Sala() {
    	
    }

    public Sala(String nom, int maxAforo) {
        nombre = nom;
        aforo = maxAforo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nom) {
        nombre = nom;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int maxAforo) {
        aforo = maxAforo;
    }
}
