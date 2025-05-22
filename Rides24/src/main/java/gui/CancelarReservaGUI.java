package gui;

import businessLogic.BLFacade;
import domain.Reserva;
import domain.Socio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CancelarReservaGUI extends JFrame {

    private final BLFacade facade;
    private Socio socio;
    private JTextArea resultadoArea;
    private List<Reserva> reservasActivas;

    public CancelarReservaGUI(BLFacade facade, Socio socio) {
        this.facade = facade;
        this.socio = socio;

        setTitle("Cancelar reserva");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton cargarBtn   = new JButton("Cargar reservas");
        JButton cancelarBtn = new JButton("Cancelar (poner nº)");
        topPanel.add(cargarBtn);
        topPanel.add(cancelarBtn);
        add(topPanel, BorderLayout.NORTH);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        cargarBtn.addActionListener((ActionEvent e) -> {
            reservasActivas = facade.verReservas(socio);
            resultadoArea.setText("");
            if (reservasActivas == null || reservasActivas.isEmpty()) {
                resultadoArea.setText("No tienes reservas activas.");
            } else {
                for (int i = 0; i < reservasActivas.size(); i++) {
                    Reserva r = reservasActivas.get(i);
                    resultadoArea.append(
                        "["+i+"] " +
                        r.getSesion().getActividad().getNombre() +
                        " | Sala: " + r.getSesion().getSala().getNombre() +
                        " | Fecha: " + r.getSesion().getFechaHora() +
                        "\n"
                    );
                }
            }
        });

        cancelarBtn.addActionListener((ActionEvent e) -> {
            if (reservasActivas == null || reservasActivas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No tienes reservas cargadas.");
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Pon el número de la reserva que quieres cancelar:");
            if (input != null) {
                try {
                    int idx = Integer.parseInt(input.trim());
                    if (idx >= 0 && idx < reservasActivas.size()) {
                        Reserva r = reservasActivas.get(idx);
                        boolean ok = facade.cancelar(socio, r.getSesion());
                        if (ok) {
                            JOptionPane.showMessageDialog(this, "Reserva cancelada.");
                            cargarBtn.doClick();
                        } else {
                            JOptionPane.showMessageDialog(this, "No se pudo cancelar.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Índice fuera de rango.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Pon un número válido.");
                }
            }
        });
    }
}
