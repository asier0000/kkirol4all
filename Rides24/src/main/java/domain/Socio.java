package domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.*;

@XmlRootElement
@Entity
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String user;
    private String pass;
    private int maxSemana;
    private int semana;
    private String email;
    private String cuenta;
    private boolean admin;

    @OneToMany(mappedBy = "socio")
    @XmlTransient
    private List<Reserva> reservas;

    public Socio() {
        reservas = new ArrayList<>();
        semana = 0;
        admin = false;
    }

    public Socio(String user, String pass, String email, String cuenta, int maxSemana) {
        this.user = user;
        this.pass = pass;
        this.email = email;
        this.cuenta = cuenta;
        this.maxSemana = maxSemana;
        this.reservas = new ArrayList<>();
        this.semana = 0;
        this.admin = false;
    }

    public Long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String u) {
        user = u;
    }

    public String getPass() {
        return pass;
    }
    public void setPass(String p) {
        pass = p;
    }

    public int getMaxSemana() {
        return maxSemana;
    }
    public void setMaxSemana(int m) {
        maxSemana = m;
    }

    public int getSemana() {
        return semana;
    }
    public void setSemana(int s) {
        semana = s;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String e) {
        email = e;
    }

    public String getCuenta() {
        return cuenta;
    }
    public void setCuenta(String c) {
        cuenta = c;
    }

    public boolean isAdmin() {
        return admin;
    }
    public void setAdmin(boolean a) {
        admin = a;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }
    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
    
    public void addReserva(Reserva r) {
        reservas.add(r);
    }
    
    public void removeReserva(Reserva r) {
        reservas.remove(r);
    }

}
