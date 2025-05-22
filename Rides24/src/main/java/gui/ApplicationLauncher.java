package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import businessLogic.BLFacade;

public class ApplicationLauncher {

    public static void main(String[] args) {
    	final BLFacade facade;
    	try {
    		URL url = new URL("http://localhost:1099/ws/KIROL4ALL?wsdl");
    		QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
    	    Service service = Service.create(url, qname);
    	    facade = service.getPort(BLFacade.class);
    	} catch (Exception e) {
    	    throw new RuntimeException("No se pudo conectar con el servidor de lógica de negocio", e);
    	}

        JFrame frame = new JFrame("KIROL4ALL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 280);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("KIROL4ALL", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        frame.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton loginButton = new JButton("Entrar");
        JButton registerButton = new JButton("Registrar");
        JButton consultarButton = new JButton("Solo consultar sesiones");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(consultarButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            new LoginGUI(facade).setVisible(true);
            frame.dispose();
        });

        registerButton.addActionListener(e -> {
            new RegisterGUI(facade).setVisible(true);
            frame.dispose();
        });

        consultarButton.addActionListener(e -> {
            new ConsultarSesionesGUI(facade, null).setVisible(true);
            frame.dispose();
        });

        frame.setVisible(true);
    }
}
