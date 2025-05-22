package gui;

import javax.swing.*;
import businessLogic.*;
import domain.*;
import java.awt.*;

public class LoginGUI extends JFrame {

    public LoginGUI(BLFacade facade) {
        setTitle("Entrar");
        setSize(300, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        JTextField usuarioField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Entrar");

        add(new JLabel("Usuario:"));
        add(usuarioField);
        add(new JLabel("Contraseña:"));
        add(passField);
        add(loginBtn);

        loginBtn.addActionListener(e -> {
            String usuario = usuarioField.getText();
            String pass = new String(passField.getPassword());

            Socio socio = facade.login(usuario, pass);
            if (socio != null) {
                JOptionPane.showMessageDialog(this, "Bienvenido " + socio.getUser());
                new SocioMenuGUI(facade, socio).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña errornea", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
