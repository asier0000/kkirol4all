package domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.*;

@XmlRootElement
@Entity
public class Reserva {

    @Id
    private String id;

    @ManyToOne
    private Socio socio;

    @ManyToOne
    private Sesion sesion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaReserva;

    private boolean espera;

    public Reserva() {
    	
    }

    public Reserva(Socio soc, Sesion ses, Date fecha, boolean estaEspera) {
        id = UUID.randomUUID().toString();
        socio = soc;
        sesion = ses;
        fechaReserva = fecha;
        espera = estaEspera;
    }

    public String getId() {
        return id;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio soc) {
        socio = soc;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion ses) {
        sesion = ses;
    }

    public Date getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Date fecha) {
        fechaReserva = fecha;
    }

    public boolean isEspera() {
        return espera;
    }

    public void setEspera(boolean estaEspera) {
        espera = estaEspera;
    }
    public void setId(String id) {
        this.id = id;
    }

}