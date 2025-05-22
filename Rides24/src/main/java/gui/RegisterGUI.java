package gui;

import javax.swing.*;
import businessLogic.*;
import domain.*;
import java.awt.*;

public class RegisterGUI extends JFrame {

    public RegisterGUI(BLFacade facade) {
        setTitle("Registro");
        setSize(350, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2));

        JTextField usuarioField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField correoField = new JTextField();
        JTextField cuentaField = new JTextField();
        JTextField maxField = new JTextField();

        JButton registerBtn = new JButton("Registrar");

        add(new JLabel("Usuario:"));
        add(usuarioField);
        add(new JLabel("Contraseña:"));
        add(passField);
        add(new JLabel("Correo:"));
        add(correoField);
        add(new JLabel("Cuenta bancaria:"));
        add(cuentaField);
        add(new JLabel("Máx. reservas/semana:"));
        add(maxField);
        add(new JLabel());
        add(registerBtn);

        registerBtn.addActionListener(e -> {
            try {
                String usuario = usuarioField.getText();
                String pass = new String(passField.getPassword());
                String correo = correoField.getText();
                String cuenta = cuentaField.getText();
                int max = Integer.parseInt(maxField.getText());

                boolean ok = facade.registrarse(usuario, pass, correo, cuenta, max);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Registrado. Inicia sesión.");
                    new LoginGUI(facade).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Ese usuario ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Pon un número válido para máximo reservas.");
            }
        });
    }
}
