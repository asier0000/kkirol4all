package businessLogic;

import java.util.*;
import domain.*;

import javax.jws.*;


@WebService
public interface BLFacade {

    @WebMethod List<Sesion> consultarSesiones(String actividad, Integer exigencia);

    @WebMethod Reserva reservar(Socio socio, Sesion sesion);

    @WebMethod boolean cancelar(Socio socio, Sesion sesion);

    @WebMethod List<Factura> generarFacturas();

    @WebMethod List<Factura> verFacturas(Socio socio);

    @WebMethod boolean pagar(String codFactura);

    @WebMethod boolean nuevaActividad(String nombre, int nivel);

    @WebMethod boolean ponerSesion(String actividad, String sala, String fechaHora);

    @WebMethod boolean registrarse(String user, String pass, String email, String cuenta, int maxSemana);

    @WebMethod Socio login(String user, String pass);

    @WebMethod List<String> actividades();

    @WebMethod List<String> salas();

    @WebMethod List<Factura> todasFacturas(Socio socio);
    
    @WebMethod List<Reserva> verReservas(Socio socio);

}
