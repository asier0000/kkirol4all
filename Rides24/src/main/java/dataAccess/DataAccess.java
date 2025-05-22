package dataAccess;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import configuration.*;
import domain.*;

public class DataAccess {
    private EntityManager db;
    private EntityManagerFactory emf;
    ConfigXML c = ConfigXML.getInstance();

    public DataAccess() {
        if (c.isDatabaseInitialized()) {
            String fileName = c.getDbFilename();
            java.io.File dbFile = new java.io.File(fileName);
            if (dbFile.exists() && dbFile.delete()) {
                System.out.println("Base de datos borrada: " + fileName);
            } else {
                System.out.println("No se pudo borrar la base de datos: " + fileName);
            }
            java.io.File backup = new java.io.File(fileName + "$");
            if (backup.exists() && backup.delete()) {
                System.out.println("Backup eliminado: " + backup.getName());
            }
        }

        open();
        if (c.isDatabaseInitialized()) {
            initializeDB();
        }
        close();
        System.out.println("DataAccess creado local=" + c.isDatabaseLocal() + ")");
    }

    public DataAccess(EntityManager db) {
        this.db = db;
    }

    public void initializeDB() {
        db.getTransaction().begin();
        try {
            Actividad yoga     = new Actividad("Yoga", 2);
            Actividad zumba    = new Actividad("Zumba", 3);
            Actividad spinning = new Actividad("Spinning", 5);
            Actividad pilates  = new Actividad("Pilates", 1);

            Sala salaA = new Sala("Sala A", 10);
            Sala salaB = new Sala("Sala B", 5);
            Sala salaC = new Sala("Sala C", 8);

            Socio usuario  = new Socio("asd", "asd", "asd@correo.com", "ES333", 10);
            Socio administrador  = new Socio("asdd", "asdd", "asdd@correo.com", "ES444", 20);
            administrador.setAdmin(true);

            db.persist(yoga);     db.persist(zumba);
            db.persist(spinning); db.persist(pilates);
            db.persist(salaA);    db.persist(salaB);    db.persist(salaC);
            db.persist(usuario);  db.persist(administrador);

            LocalDateTime lunes     = LocalDate.now().with(DayOfWeek.MONDAY).atTime(10,0);
            LocalDateTime martes    = LocalDate.now().with(DayOfWeek.TUESDAY).atTime(11,0);
            LocalDateTime miercoles = LocalDate.now().with(DayOfWeek.WEDNESDAY).atTime(12,0);
            LocalDateTime jueves    = LocalDate.now().with(DayOfWeek.THURSDAY).atTime(9,0);
            LocalDateTime viernes   = LocalDate.now().with(DayOfWeek.FRIDAY).atTime(18,0);

            Sesion s1 = new Sesion(yoga, salaA, lunes);
            Sesion s2 = new Sesion(zumba, salaB, martes);
            Sesion s3 = new Sesion(spinning, salaC, miercoles);
            Sesion s4 = new Sesion(pilates, salaA, jueves);
            Sesion s5 = new Sesion(yoga, salaB, viernes);

            db.persist(s1); db.persist(s2); db.persist(s3); db.persist(s4); db.persist(s5);

            db.getTransaction().commit();
            System.out.println("db montada con datos de prueba");
        } catch (Exception e) {
            e.printStackTrace();
            db.getTransaction().rollback();
            System.out.println("Error al inicializar la base de datos.");
        }
    }

    public void open() {
        String fileName = c.getDbFilename();
        if (c.isDatabaseLocal()) {
            emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
            db = emf.createEntityManager();
        } else {
            Map<String, String> props = new HashMap<>();
            props.put("javax.persistence.jdbc.user", c.getUser());
            props.put("javax.persistence.jdbc.password", c.getPassword());
            emf = Persistence.createEntityManagerFactory(
                "objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, props
            );
            db = emf.createEntityManager();
        }
        System.out.println("DataAccess abierto (local=" + c.isDatabaseLocal() + ")");
    }

    public void close() {
        db.close();
        System.out.println("DataAccess cerrado");
    }

    public Socio getSocioByUser(String user) {
        TypedQuery<Socio> q = db.createQuery(
            "SELECT s FROM Socio s WHERE s.user = :user", Socio.class
        );
        q.setParameter("user", user);
        List<Socio> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public List<Factura> getFacturasDeSocio(Socio socio) {
        return db.createQuery(
            "SELECT f FROM Factura f WHERE f.socio.id = :socioId AND f.estaPagada = false", Factura.class)
            .setParameter("socioId", socio.getId())
            .getResultList();
    }

    public Reserva getReservaPorId(String id) {
        return db.find(Reserva.class, id);
    }

    public boolean tieneReservaActiva(Socio socio, Sesion sesion) {
        TypedQuery<Reserva> q = db.createQuery(
            "SELECT r FROM Reserva r WHERE r.socio.id = :socioId AND r.sesion.id = :sesionId AND r.espera = false",
            Reserva.class
        );
        q.setParameter("socioId", socio.getId());
        q.setParameter("sesionId", sesion.getId());
        return !q.getResultList().isEmpty();
    }

    public Reserva getReservaActivaDeSesion(Socio socio, Sesion sesion) {
        TypedQuery<Reserva> q = db.createQuery(
            "SELECT r FROM Reserva r WHERE r.socio.id = :socioId AND r.sesion.id = :sesionId AND r.espera = false",
            Reserva.class
        );
        q.setParameter("socioId", socio.getId());
        q.setParameter("sesionId", sesion.getId());
        List<Reserva> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public long contarReservasSemana(Socio socio, LocalDate lunes, LocalDate domingo) {
        Date desde = Date.from(lunes.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date hasta = Date.from(domingo.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant());
        TypedQuery<Long> q = db.createQuery(
            "SELECT COUNT(r) FROM Reserva r WHERE r.socio.id = :socioId AND r.espera = false AND r.fechaReserva BETWEEN :inicio AND :fin",
            Long.class
        );
        q.setParameter("socioId", socio.getId());
        q.setParameter("inicio", desde);
        q.setParameter("fin", hasta);
        return q.getSingleResult();
    }

    public boolean tieneReservaEnEspera(Socio socio, Sesion sesion) {
        TypedQuery<Reserva> q = db.createQuery(
            "SELECT r FROM Reserva r WHERE r.socio.id = :socioId AND r.sesion.id = :sesionId AND r.espera = true",
            Reserva.class
        );
        q.setParameter("socioId", socio.getId());
        q.setParameter("sesionId", sesion.getId());
        return !q.getResultList().isEmpty();
    }

    public List<Reserva> getReservasEnEsperaPorSesionOrdenadas(Sesion sesion) {
        TypedQuery<Reserva> q = db.createQuery(
            "SELECT r FROM Reserva r WHERE r.sesion.id = :sesionId AND r.espera = true ORDER BY r.fechaReserva ASC",
            Reserva.class
        );
        q.setParameter("sesionId", sesion.getId());
        return q.getResultList();
    }

    public List<Sesion> getSesiones(String nombreActividad, Integer exigencia) {
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(java.time.DayOfWeek.MONDAY);
        LocalDate domingo = hoy.with(java.time.DayOfWeek.SUNDAY);
        Date inicioSemana = Date.from(lunes.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date finSemana = Date.from(domingo.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant());

        String queryStr = "SELECT s FROM Sesion s WHERE s.fechaHora BETWEEN :inicio AND :fin";
        if (nombreActividad != null) {
            queryStr += " AND s.actividad.nombre = :nombreActividad";
        }
        if (exigencia != null) {
            queryStr += " AND s.actividad.nivelExigencia = :exigencia";
        }
        TypedQuery<Sesion> q = db.createQuery(queryStr, Sesion.class);
        q.setParameter("inicio", inicioSemana);
        q.setParameter("fin", finSemana);
        if (nombreActividad != null) {
            q.setParameter("nombreActividad", nombreActividad);
        }
        if (exigencia != null) {
            q.setParameter("exigencia", exigencia);
        }
        return q.getResultList();
    }

    public Sesion getSesionById(Long id) {
        return db.find(Sesion.class, id);
    }

    public Actividad getActividadByNombre(String nombre) {
        TypedQuery<Actividad> q = db.createQuery(
            "SELECT a FROM Actividad a WHERE a.nombre = :nombre", Actividad.class);
        q.setParameter("nombre", nombre);
        List<Actividad> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public void storeActividad(Actividad actividad) {
        db.getTransaction().begin();
        db.persist(actividad);
        db.getTransaction().commit();
    }

    public Sala getSalaByNombre(String nombre) {
        TypedQuery<Sala> q = db.createQuery(
            "SELECT s FROM Sala s WHERE s.nombre = :nombre", Sala.class);
        q.setParameter("nombre", nombre);
        List<Sala> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public void storeSala(Sala sala) {
        db.getTransaction().begin();
        db.persist(sala);
        db.getTransaction().commit();
    }

    public void storeSocio(Socio socio) {
        db.getTransaction().begin();
        db.persist(socio);
        db.getTransaction().commit();
    }

    public List<Actividad> getTodasLasActividades() {
        return db.createQuery("SELECT a FROM Actividad a", Actividad.class).getResultList();
    }

    public List<Sala> getTodasLasSalas() {
        return db.createQuery("SELECT s FROM Sala s", Sala.class).getResultList();
    }

    public List<Socio> getTodosLosSocios() {
        return db.createQuery("SELECT s FROM Socio s", Socio.class).getResultList();
    }

    public Factura getFacturaPorCodigo(String codigo) {
        TypedQuery<Factura> q = db.createQuery(
            "SELECT f FROM Factura f WHERE f.codigo = :codigo", Factura.class);
        q.setParameter("codigo", codigo);
        List<Factura> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }
    
    public List<Factura> getFacturasDeSocioRaw(Socio socio) {
        return db.createQuery(
            "SELECT f FROM Factura f WHERE f.socio.id = :socioId", Factura.class)
            .setParameter("socioId", socio.getId())
            .getResultList();
    }
    
    public EntityManager getDb() {
        return db;
    }
    
    public Sesion getSesionCampos(String actividadNombre, String salaNombre, Date fecha) {
        TypedQuery<Sesion> q = db.createQuery(
            "SELECT s FROM Sesion s WHERE s.actividad.nombre = :act AND s.sala.nombre = :sal AND s.fechaHora = :fecha",
            Sesion.class);
        q.setParameter("act", actividadNombre);
        q.setParameter("sal", salaNombre);
        q.setParameter("fecha", fecha);
        List<Sesion> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }
    
    public List<Reserva> getReservasDeSocio(Socio socio) {
        TypedQuery<Reserva> q = db.createQuery(
            "SELECT r FROM Reserva r WHERE r.socio.id = :socioId", Reserva.class);
        q.setParameter("socioId", socio.getId());
        return q.getResultList();
    }
    
    public List<Factura> getFacturasDeSocioPorUser(String user) {
        return db.createQuery(
            "SELECT f FROM Factura f WHERE f.socio.user = :user AND f.estaPagada = false", Factura.class)
            .setParameter("user", user)
            .getResultList();
    }
}
