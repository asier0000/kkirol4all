package gui;

import businessLogic.*;
import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GenerarFacturasGUI extends JFrame {

    private BLFacade facade;
    private JTextArea resultadoArea;

    public GenerarFacturasGUI(BLFacade facade) {
        this.facade = facade;

        setTitle("Generar facturas");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton generarBtn = new JButton("Generar");
        add(generarBtn, BorderLayout.NORTH);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        generarBtn.addActionListener((ActionEvent e) -> {
            List<Factura> facturas = facade.generarFacturas();
            resultadoArea.setText("");

            if (facturas.isEmpty()) {
                resultadoArea.setText("No se ha generado ninguna factura.");
            } else {
                for (Factura f : facturas) {
                    resultadoArea.append("Código: " + f.getCodigo() + "\n");
                    resultadoArea.append("Total: " + f.getTotal() + " €\n");
                    resultadoArea.append("Reservas:\n");
                    for (Reserva r : f.getReservas()) {
                        resultadoArea.append("- " + r.getSesion().getActividad().getNombre() +
                                " el " + r.getSesion().getFechaHora() + "\n");
                    }
                    resultadoArea.append("-----\n");
                }
            }
        });
    }
}
