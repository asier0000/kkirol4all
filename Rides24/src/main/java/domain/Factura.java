package domain;

import javax.persistence.*;
import java.util.*;

@Entity
public class Factura {

    @Id
    private String codigo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "socio_id", referencedColumnName = "id")
    private Socio socio;

    @ManyToMany
    private List<Reserva> reservas;

    private double total;
    private boolean estaPagada;

    public Factura() { 
        this.reservas = new ArrayList<>();
    }

    public Factura(String cod, Date fec, Socio soc) {
        codigo = cod;
        fecha = fec;
        socio = soc;
        reservas = new ArrayList<>();
        total = 0;
        estaPagada = false;
    }

    public void meterReserva(Reserva reserva, double precio) {
        reservas.add(reserva);
        total += precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String cod) {
        codigo = cod;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fec) {
        fecha = fec;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio soc) {
        socio = soc;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isEstaPagada() {
        return estaPagada;
    }

    public void setEstaPagada(boolean pagada) {
        estaPagada = pagada;
    }
}