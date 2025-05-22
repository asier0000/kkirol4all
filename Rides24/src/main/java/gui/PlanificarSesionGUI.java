package gui;

import businessLogic.*;
import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PlanificarSesionGUI extends JFrame {

    private BLFacade facade;
    private JComboBox<String> actividadBox;
    private JComboBox<String> salaBox;
    private JTextField fechaHoraField;

    public PlanificarSesionGUI(BLFacade facade) {
        this.facade = facade;

        setTitle("Planificar sesión");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Actividad:"));
        actividadBox = new JComboBox<>();
        List<String> actividades = facade.actividades();
        for (String nombre : actividades) {
            actividadBox.addItem(nombre);
        }
        add(actividadBox);

        add(new JLabel("Sala:"));
        salaBox = new JComboBox<>();
        List<String> salas = facade.salas();
        for (String nombre : salas) {
            salaBox.addItem(nombre);
        }
        add(salaBox);

        add(new JLabel("Fecha y hora (yyyy-MM-ddTHH:mm):"));
        fechaHoraField = new JTextField();
        add(fechaHoraField);

        JButton planificarBtn = new JButton("Planificar");
        add(new JLabel());
        add(planificarBtn);

        planificarBtn.addActionListener((ActionEvent e) -> {
            String actividad = (String) actividadBox.getSelectedItem();
            String sala = (String) salaBox.getSelectedItem();
            String fechaHora = fechaHoraField.getText().trim();

            boolean ok = facade.ponerSesion(actividad, sala, fechaHora);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Sesión guardada.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar. Mira los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
