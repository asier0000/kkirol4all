package gui;

import businessLogic.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AñadirActividadGUI extends JFrame {

    private BLFacade facade;
    private JTextField nombreField;
    private JTextField exigenciaField;

    public AñadirActividadGUI(BLFacade facade) {
        this.facade = facade;

        setTitle("Añadir actividad");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Nombre:"));
        nombreField = new JTextField();
        add(nombreField);

        add(new JLabel("Exigencia (1-5):"));
        exigenciaField = new JTextField();
        add(exigenciaField);

        JButton añadirBtn = new JButton("Añadir");
        add(añadirBtn);

        añadirBtn.addActionListener((ActionEvent e) -> {
            String nombre = nombreField.getText().trim();
            int nivel;

            try {
                nivel = Integer.parseInt(exigenciaField.getText());
                if (nivel < 1 || nivel > 5) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Exigencia mal puesta (1-5)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = facade.nuevaActividad(nombre, nivel);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Actividad añadida.");
            } else {
                JOptionPane.showMessageDialog(this, "Esa actividad ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
