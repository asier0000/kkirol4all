package gui;

import businessLogic.*;
import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ConsultarSesionesGUI extends JFrame {

    private BLFacade facade;
    private Socio socio;
    private JTextArea resultadoArea;
    private List<Sesion> sesiones;

    public ConsultarSesionesGUI(BLFacade facade, Socio socio) {
        this.facade = facade;
        this.socio = socio;

        setTitle("Consultar sesiones");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Sesiones esta semana", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        sesiones = facade.consultarSesiones(null, null);
        if (sesiones == null || sesiones.isEmpty()) {
            resultadoArea.setText("No hay sesiones esta semana.");
        } else {
            int index = 0;
            for (Sesion s : sesiones) {
                resultadoArea.append("[" + index + "] "
                        + s.getActividad().getNombre()
                        + " | Exigencia: " + s.getActividad().getNivelExigencia()
                        + " | Sala: " + s.getSala().getNombre()
                        + " | Fecha: " + s.getFechaHora()
                        + " | Ocupación: " + s.getOcupadas() + "/" + s.getSala().getAforo()
                        + "\n"
                );
                index++;
            }
        }

        if (socio != null) {
            JButton reservarBtn = new JButton("Reservar");
            add(reservarBtn, BorderLayout.SOUTH);

            reservarBtn.addActionListener((ActionEvent e) -> {
                String input = JOptionPane.showInputDialog(this, "Pon el número de la sesión que quieres reservar:");
                if (input != null && !input.isBlank()) {
                    try {
                        int idx = Integer.parseInt(input.trim());
                        if (idx >= 0 && idx < sesiones.size()) {
                            Sesion seleccionada = sesiones.get(idx);
                            if (seleccionada != null && socio.getUser() != null) {
                                Reserva r = facade.reservar(socio, seleccionada);
                                if (r != null) {
                                    if (r.isEspera()) {
                                        JOptionPane.showMessageDialog(this, "Reserva en lista de espera.");
                                    } else {
                                        JOptionPane.showMessageDialog(this, "Reserva hecha.");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(this, "No se pudo reservar.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "Datos de sesión o socio inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Índice fuera de rango.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Pon un número válido.");
                    }
                }
            });
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        } else {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }
}
