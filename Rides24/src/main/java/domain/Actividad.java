package domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Actividad {

    @Id
    private String nombre;
    private int nivelExigencia;

    public Actividad() {
    	
    }

    public Actividad(String nom, int exigencia) {
        nombre = nom;
        nivelExigencia = exigencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nom) {
        nombre = nom;
    }

    public int getNivelExigencia() {
        return nivelExigencia;
    }

    public void setNivelExigencia(int exigencia) {
        nivelExigencia = exigencia;
    }
}
