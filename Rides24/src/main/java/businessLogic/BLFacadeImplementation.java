package businessLogic;

import java.util.*;
import java.time.*;
import domain.*;
import javax.jws.*;
import dataAccess.DataAccess;

@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation implements BLFacade {
    DataAccess dbManager;

    public BLFacadeImplementation() {
        System.out.println("instancia BLFacadeImplementation creada");
        dbManager = new DataAccess();
    }

    public BLFacadeImplementation(DataAccess da) {
        System.out.println("instancia BLFacadeImplementation creada con DataAccess dado");
        dbManager = da;
    }

    @Override
    public boolean registrarse(String user, String pass, String email, String cuenta, int maxSemana) {
        try {
            dbManager.open();

            if (dbManager.getSocioByUser(user) != null) {
                dbManager.close();
                return false;
            }

            Socio nuevo = new Socio(user, pass, email, cuenta, maxSemana);
            dbManager.storeSocio(nuevo);

            dbManager.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Socio login(String user, String pass) {
        try {
            dbManager.open();
            Socio socio = dbManager.getSocioByUser(user);
            dbManager.close();

            if (socio != null && socio.getPass().equals(pass)) {
                socio.setReservas(null);
                return socio;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod
    public List<Sesion> consultarSesiones(String actividad, Integer exigencia) {
        dbManager.open();
        List<Sesion> resultado = dbManager.getSesiones(actividad, exigencia);
        dbManager.close();
        return resultado;
    }

    @Override
    public Reserva reservar(Socio socio, Sesion sesion) {
        try {
            System.out.println("reservar() llamado con socio=" + socio.getUser() + ", sesionId=" + sesion.getId());
            dbManager.open();

            Socio socioDB = dbManager.getSocioByUser(socio.getUser());
            System.out.println("  socioDB=" + (socioDB != null ? socioDB.getUser() : "null"));

            Sesion sesionDB;
            Long sid = sesion.getId();
            if (sid != null) {
                sesionDB = dbManager.getSesionById(sid);
            } else {
                sesionDB = dbManager.getSesionCampos(
                    sesion.getActividad().getNombre(),
                    sesion.getSala().getNombre(),
                    sesion.getFechaHora()
                );
            }
            System.out.println("  sesionDB=" + (sesionDB != null ? sesionDB.getId() : "null"));

            if (socioDB == null || sesionDB == null) {
                System.out.println("  abortando: socioDB o sesionDB es null");
                dbManager.close();
                return null;
            }

            boolean tieneActiva = dbManager.tieneReservaActiva(socioDB, sesionDB);
            boolean tieneEspera = dbManager.tieneReservaEnEspera(socioDB, sesionDB);
            System.out.println("  tieneReservaActiva=" + tieneActiva + ", tieneReservaEnEspera=" + tieneEspera);
            if (tieneActiva || tieneEspera) {
                System.out.println("  abortando: ya existe reserva");
                dbManager.close();
                return null;
            }

            LocalDate hoy = LocalDate.now();
            LocalDate lunes = hoy.with(DayOfWeek.MONDAY);
            LocalDate domingo = hoy.with(DayOfWeek.SUNDAY);
            long numSemana = dbManager.contarReservasSemana(socioDB, lunes, domingo);
            boolean libre = sesionDB.quedanSitios();
            System.out.println("  numSemana=" + numSemana + ", quedanSitios=" + libre);

            Date ahora = new Date();
            Reserva nueva;
            dbManager.getDb().getTransaction().begin();
            if (numSemana < socioDB.getMaxSemana() && libre) {
                nueva = new Reserva(socioDB, sesionDB, ahora, false);
                sesionDB.sumarPlaza();
                socioDB.addReserva(nueva);
                dbManager.getDb().persist(nueva);
                dbManager.getDb().merge(socioDB);
                dbManager.getDb().merge(sesionDB);
                System.out.println("  creando reserva activa");
            } else {
                nueva = new Reserva(socioDB, sesionDB, ahora, true);
                socioDB.addReserva(nueva);
                dbManager.getDb().persist(nueva);
                dbManager.getDb().merge(socioDB);
                System.out.println("  creando reserva en espera");
            }
            dbManager.getDb().getTransaction().commit();
            dbManager.close();

            Reserva salida = new Reserva();
            salida.setId(nueva.getId());
            salida.setEspera(nueva.isEspera());
            salida.setFechaReserva(nueva.getFechaReserva());

            Sesion sesionSimple = new Sesion();
            sesionSimple.setId(nueva.getSesion().getId());
            sesionSimple.setFechaHora(nueva.getSesion().getFechaHora());
            sesionSimple.setActividad(nueva.getSesion().getActividad());
            sesionSimple.setSala(nueva.getSesion().getSala());

            salida.setSesion(sesionSimple);

            return salida;

        } catch (Exception e) {
            e.printStackTrace();
            try { dbManager.close(); } catch (Exception ex) {}
            return null;
        }
    }

    @Override
    public boolean cancelar(Socio socio, Sesion sesion) {
        dbManager.open();

        socio = dbManager.getSocioByUser(socio.getUser());
        sesion = dbManager.getSesionById(sesion.getId());

        Reserva activa = dbManager.getReservaActivaDeSesion(socio, sesion);

        if (activa != null) {
            dbManager.getDb().getTransaction().begin();

            socio.removeReserva(activa);
            sesion.quitarPlaza();
            dbManager.getDb().remove(activa);

            List<Reserva> enEspera = dbManager.getReservasEnEsperaPorSesionOrdenadas(sesion);
            if (!enEspera.isEmpty()) {
                Reserva promovida = enEspera.get(0);
                promovida.setEspera(false);
                promovida.getSesion().sumarPlaza();

                promovida.getSocio().setSemana(
                    promovida.getSocio().getSemana() + 1
                );

                dbManager.getDb().merge(promovida.getSocio());
                dbManager.getDb().merge(promovida);
                dbManager.getDb().merge(promovida.getSesion());
            }

            dbManager.getDb().merge(socio);
            dbManager.getDb().merge(sesion);
            dbManager.getDb().getTransaction().commit();
            dbManager.close();

            System.out.println("cancelación hecha");
            return true;
        }

        dbManager.close();
        return false;
    }

    @Override
    public List<Factura> generarFacturas() {
        try {
            dbManager.open();
            dbManager.getDb().getTransaction().begin();
            List<Socio> socios = dbManager.getTodosLosSocios();
            List<Factura> nuevas = new ArrayList<>();
            Date hoy = new Date();

            for (Socio socio : socios) {
                Socio sDB = dbManager.getSocioByUser(socio.getUser());
                if (sDB == null) continue;

                List<Reserva> facturables = sDB.getReservas().stream()
                    .filter(r -> !r.isEspera() &&
                            r.getFechaReserva().after(new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000)))
                    .toList();

                System.out.println("Socio " + sDB.getUser() + " tiene " + facturables.size() + " reservas facturables esta semana.");

                if (facturables.size() > 4) {
                    Factura f = new Factura("F-" + System.currentTimeMillis(), hoy, sDB);
                    f.setEstaPagada(false);

                    for (int i = 4; i < facturables.size(); i++) {
                        Reserva r = facturables.get(i);
                        Reserva rDB = dbManager.getReservaPorId(r.getId());
                        f.meterReserva(rDB, 10.0);
                    }

                    if (f.getTotal() > 0) {
                        dbManager.getDb().persist(f);
                        System.out.println("Factura persistida: " + f.getCodigo() + " para socio " + sDB.getUser() + " (ID: " + sDB.getId() + ") estaPagada: " + f.isEstaPagada());
                        nuevas.add(f);
                    }
                }

                sDB.setSemana(0);
                dbManager.getDb().merge(sDB);
            }

            dbManager.getDb().getTransaction().commit();
            dbManager.close();
            
            List<Factura> salida = new ArrayList<>();
            for (Factura orig : nuevas) {
                Factura f = new Factura();
                f.setCodigo(orig.getCodigo());
                f.setFecha(orig.getFecha());
                f.setTotal(orig.getTotal());
                f.setEstaPagada(orig.isEstaPagada());
                List<Reserva> reservasPlanas = new ArrayList<>();
                if (orig.getReservas() != null) {
                    for (Reserva r : orig.getReservas()) {
                        if (r != null) {
                            Reserva rPlano = new Reserva();
                            rPlano.setId(r.getId());
                            rPlano.setEspera(r.isEspera());
                            rPlano.setFechaReserva(r.getFechaReserva());
                            if (r.getSesion() != null) {
                                Sesion s = new Sesion();
                                s.setId(r.getSesion().getId());
                                s.setFechaHora(r.getSesion().getFechaHora());
                                s.setActividad(r.getSesion().getActividad());
                                s.setSala(r.getSesion().getSala());
                                rPlano.setSesion(s);
                            }
                            reservasPlanas.add(rPlano);
                        }
                    }
                }
                f.setReservas(reservasPlanas);
                salida.add(f);
            }
            return salida;
        } catch (Exception e) {
            e.printStackTrace();
            try { dbManager.close(); } catch (Exception ex) {}
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Factura> verFacturas(Socio socio) {
        try {
            dbManager.open();
            Socio realSocio = dbManager.getSocioByUser(socio.getUser());
            if (realSocio == null) {
                System.out.println("No se encontró el socio " + socio.getUser());
                dbManager.close();
                return new ArrayList<>();
            }
            System.out.println("Buscando facturas para usuario: " + realSocio.getUser() + " (ID: " + realSocio.getId() + ")");
            List<Factura> originales = dbManager.getFacturasDeSocio(realSocio);
            System.out.println("Facturas encontradas: " + originales.size());
            for (Factura f : originales) {
                System.out.println("Factura encontrada: " + f.getCodigo() + " | estaPagada: " + f.isEstaPagada() + " | Socio: " + f.getSocio().getUser() + " | Socio ID: " + f.getSocio().getId());
            }
            dbManager.close();

            List<Factura> salida = new ArrayList<>();
            for (Factura f : originales) {
                Factura copia = new Factura();
                copia.setCodigo(f.getCodigo());
                copia.setFecha(f.getFecha());
                copia.setTotal(f.getTotal());
                copia.setEstaPagada(f.isEstaPagada());
                List<Reserva> reservasPlanas = new ArrayList<>();
                if (f.getReservas() != null) {
                    for (Reserva r : f.getReservas()) {
                        if (r != null) {
                            Reserva rPlano = new Reserva();
                            rPlano.setId(r.getId());
                            rPlano.setEspera(r.isEspera());
                            rPlano.setFechaReserva(r.getFechaReserva());
                            if (r.getSesion() != null) {
                                Sesion s = new Sesion();
                                s.setId(r.getSesion().getId());
                                s.setFechaHora(r.getSesion().getFechaHora());
                                s.setActividad(r.getSesion().getActividad());
                                s.setSala(r.getSesion().getSala());
                                rPlano.setSesion(s);
                            }
                            reservasPlanas.add(rPlano);
                        }
                    }
                }
                copia.setReservas(reservasPlanas);
                salida.add(copia);
            }
            return salida;
        } catch (Exception e) {
            e.printStackTrace();
            try { dbManager.close(); } catch (Exception ex) {}
            return new ArrayList<>();
        }
    }

    @Override
    public boolean pagar(String codFactura) {
        dbManager.open();

        Factura f = dbManager.getFacturaPorCodigo(codFactura);
        if (f != null && !f.isEstaPagada()) {
            dbManager.getDb().getTransaction().begin();
            f.setEstaPagada(true);
            dbManager.getDb().merge(f);
            dbManager.getDb().getTransaction().commit();
            dbManager.close();
            return true;
        }

        dbManager.close();
        return false;
    }

    @Override
    public boolean nuevaActividad(String nombre, int nivel) {
        dbManager.open();

        Actividad ya = dbManager.getActividadByNombre(nombre);
        if (ya != null) {
            dbManager.close();
            return false;
        }

        Actividad nueva = new Actividad(nombre, nivel);
        dbManager.storeActividad(nueva);

        dbManager.close();
        return true;
    }

    @Override
    public boolean ponerSesion(String actividad, String sala, String fechaHora) {
        dbManager.open();

        Actividad act = dbManager.getActividadByNombre(actividad);
        Sala s = dbManager.getSalaByNombre(sala);

        if (act == null || s == null) {
            dbManager.close();
            return false;
        }

        try {
            LocalDateTime fecha = LocalDateTime.parse(fechaHora);
            Sesion nuevaSesion = new Sesion(act, s, fecha);

            dbManager.getDb().getTransaction().begin();
            dbManager.getDb().persist(nuevaSesion);
            dbManager.getDb().getTransaction().commit();

            System.out.println("sesión guardada.");
            dbManager.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (dbManager.getDb().getTransaction().isActive()) {
                dbManager.getDb().getTransaction().rollback();
            }
            dbManager.close();
            return false;
        }
    }

    @Override
    public List<String> actividades() {
        dbManager.open();
        List<Actividad> actividades = dbManager.getTodasLasActividades();
        List<String> nombres = actividades.stream().map(Actividad::getNombre).toList();
        dbManager.close();
        return nombres;
    }

    @Override
    public List<String> salas() {
        dbManager.open();
        List<Sala> salas = dbManager.getTodasLasSalas();
        List<String> nombres = salas.stream().map(Sala::getNombre).toList();
        dbManager.close();
        return nombres;
    }

    public DataAccess getDbManager() {
        return dbManager;
    }

    @Override
    public List<Factura> todasFacturas(Socio socio) {
        dbManager.open();
        Socio socioDB = dbManager.getSocioByUser(socio.getUser());
        List<Factura> facturas = dbManager.getFacturasDeSocioRaw(socioDB);
        dbManager.close();
        return facturas;
    }
    
    @Override
    public List<Reserva> verReservas(Socio socio) {
        try {
            System.out.println("verReservas() llamado con socio=" + socio.getUser());
            dbManager.open();

            Socio socioDB = dbManager.getSocioByUser(socio.getUser());
            System.out.println("  socioDB=" + (socioDB != null ? socioDB.getUser() : "null"));

            if (socioDB == null) {
                dbManager.close();
                return null;
            }

            List<Reserva> lista = dbManager.getReservasDeSocio(socioDB);
            System.out.println("  devolviendo " + (lista != null ? lista.size() : "null") + " reservas");

            List<Reserva> salida = new ArrayList<>();
            for (Reserva r : lista) {
                Reserva copia = new Reserva();
                copia.setEspera(r.isEspera());
                copia.setFechaReserva(r.getFechaReserva());
                copia.setId(r.getId());

                Sesion s = new Sesion();
                s.setId(r.getSesion().getId());
                s.setFechaHora(r.getSesion().getFechaHora());
                s.setActividad(r.getSesion().getActividad());
                s.setSala(r.getSesion().getSala());
                copia.setSesion(s);

                salida.add(copia);
            }

            dbManager.close();
            return salida;
        } catch (Exception e) {
            e.printStackTrace();
            try { dbManager.close(); } catch (Exception ex) {}
            return null;
        }
    }
    
    
}
