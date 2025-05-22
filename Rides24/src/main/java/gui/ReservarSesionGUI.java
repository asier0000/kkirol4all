package gui;

import businessLogic.*;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ReservarSesionGUI extends JFrame {

    private BLFacade facade;
    private Socio socio;
    private boolean modoVisitante;

    private JTextField nombreActividadField;
    private JTextField exigenciaField;
    private JTextArea resultadoArea;
    private List<Sesion> sesionesResultado;

    public ReservarSesionGUI(BLFacade facade, Socio socio) {
        this.facade = facade;
        this.socio = socio;
        this.modoVisitante = (socio == null);

        setTitle("Consultar sesiones" + (modoVisitante ? " (visitante)" : ""));
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Nombre actividad:"));
        nombreActividadField = new JTextField();
        inputPanel.add(nombreActividadField);

        inputPanel.add(new JLabel("Exigencia:"));
        exigenciaField = new JTextField();
        inputPanel.add(exigenciaField);

        JButton buscarBtn = new JButton("Buscar");
        inputPanel.add(buscarBtn);

        add(inputPanel, BorderLayout.NORTH);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        JButton reservarBtn = new JButton("Reservar (numero)");
        inputPanel.add(reservarBtn);

        buscarBtn.addActionListener((ActionEvent e) -> {
            String nombre = nombreActividadField.getText().trim();
            String exigenciaStr = exigenciaField.getText().trim();
            Integer exigencia = null;

            if (!exigenciaStr.isEmpty()) {
                try {
                    exigencia = Integer.parseInt(exigenciaStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Exigencia mal puesta");
                    return;
                }
            }

            sesionesResultado = facade.consultarSesiones(
                nombre.isEmpty() ? null : nombre, exigencia);

            resultadoArea.setText("");
            if (sesionesResultado.isEmpty()) {
                resultadoArea.setText("No hay sesiones.");
            } else {
                int index = 0;
                for (Sesion s : sesionesResultado) {
                    resultadoArea.append("[" + index + "] " +
                        s.getActividad().getNombre() + " - Exigencia: " + s.getActividad().getNivelExigencia() +
                        " - Sala: " + s.getSala().getNombre() +
                        " - Fecha: " + s.getFechaHora() + "\n"
                    );
                    index++;
                }
            }
        });

        reservarBtn.addActionListener((ActionEvent e) -> {
            System.out.println("Clic en botón Reservar");
            if (modoVisitante) {
                System.out.println("Modo visitante, no puede reservar");
                JOptionPane.showMessageDialog(this, "Tienes que iniciar sesión para reservar.");
                return;
            }

            String input = JOptionPane.showInputDialog(this, "Pon el número de la sesión a reservar:");
            if (input != null) {
                try {
                    int idx = Integer.parseInt(input);
                    System.out.println("Índice seleccionado: " + idx);
                    if (idx >= 0 && idx < sesionesResultado.size()) {
                        Sesion seleccionada = sesionesResultado.get(idx);

                        // ¡CAMBIO AQUÍ!
                        Socio socioPlano = new Socio();
                        socioPlano.setUser(socio.getUser());

                        Sesion sesionPlano = new Sesion();
                        sesionPlano.setId(seleccionada.getId());

                        System.out.println("Llamando a facade.reservar...");
                        Reserva r = facade.reservar(socioPlano, sesionPlano);

                        if (r != null) {
                            System.out.println("Reserva no nula");
                            if (r.isEspera()) {
                                System.out.println("Reserva en espera");
                                JOptionPane.showMessageDialog(this, "Reserva en espera.");
                            } else {
                                System.out.println("Reserva hecha");
                                JOptionPane.showMessageDialog(this, "Reserva hecha.");
                            }
                        } else {
                            System.out.println("No se pudo reservar (devuelve null)");
                            JOptionPane.showMessageDialog(this, "No se pudo reservar.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        System.out.println("Índice fuera de rango");
                        JOptionPane.showMessageDialog(this, "Fuera de rango.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Número inválido");
                    JOptionPane.showMessageDialog(this, "Pon un número válido.");
                }
            } else {
                System.out.println("Input fue null (cancelado)");
            }
        });
    }
}
