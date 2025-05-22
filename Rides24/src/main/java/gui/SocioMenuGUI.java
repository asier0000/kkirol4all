package gui;

import businessLogic.*;
import domain.*;
import javax.swing.*;

public class SocioMenuGUI extends JFrame {

    private BLFacade facade;
    private Socio socio;

    public SocioMenuGUI(BLFacade facade, Socio socio) {
        this.facade = facade;
        this.socio = socio;

        setTitle("KIROL4ALL");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Bienvenido, " + socio.getUser());
        panel.add(welcomeLabel);

        JButton consultarSesionesBtn = new JButton("Consultar sesiones");
        JButton cancelarBtn = new JButton("Cancelar reserva");
        JButton facturasBtn = new JButton("Ver facturas");

        panel.add(consultarSesionesBtn);
        panel.add(cancelarBtn);
        panel.add(facturasBtn);

        if (socio.isAdmin()) {
            JButton generarFacturasBtn = new JButton("Generar facturas");
            JButton añadirActividadBtn = new JButton("Añadir actividad");
            JButton planificarSesionBtn = new JButton("Planificar sesión");

            panel.add(generarFacturasBtn);
            panel.add(añadirActividadBtn);
            panel.add(planificarSesionBtn);

            generarFacturasBtn.addActionListener(e -> new GenerarFacturasGUI(facade).setVisible(true));
            añadirActividadBtn.addActionListener(e -> new AñadirActividadGUI(facade).setVisible(true));
            planificarSesionBtn.addActionListener(e -> new PlanificarSesionGUI(facade).setVisible(true));
        }

        add(panel);

        consultarSesionesBtn.addActionListener(e -> new ConsultarSesionesGUI(facade, socio).setVisible(true));
        cancelarBtn.addActionListener(e -> new CancelarReservaGUI(facade, socio).setVisible(true));
        facturasBtn.addActionListener(e -> new ConsultarFacturasGUI(facade, socio).setVisible(true));
    }
}
