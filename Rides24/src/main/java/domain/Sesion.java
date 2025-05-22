package domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Sesion {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Actividad actividad;

    @ManyToOne
    private Sala sala;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHora;

    private int ocupadas;

    public Sesion() {
        ocupadas = 0;
    }

    public Sesion(Actividad act, Sala sal, java.time.LocalDateTime fecha) {
        actividad = act;
        sala = sal;
        fechaHora = java.sql.Timestamp.valueOf(fecha);
        ocupadas = 0;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Actividad getActividad() {
        return actividad;
    }
    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Sala getSala() {
        return sala;
    }
    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Date getFechaHora() {
        return fechaHora;
    }
    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getOcupadas() {
        return ocupadas;
    }
    public void setOcupadas(int ocupadas) {
        this.ocupadas = ocupadas;
    }

    public boolean quedanSitios() {
        return ocupadas < sala.getAforo();
    }

    public void sumarPlaza() {
        if (quedanSitios()) {
            ocupadas++;
        }
    }

    public void quitarPlaza() {
        if (ocupadas > 0) {
            ocupadas--;
        }
    }

    @Override
    public String toString() {
        return "Sesion{id=" + id + ", fecha=" + fechaHora +
               ", actividad=" + (actividad != null ? actividad.getNombre() : "null") +
               "}";
    }
}
