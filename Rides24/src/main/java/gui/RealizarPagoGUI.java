package gui;

import businessLogic.*;
import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RealizarPagoGUI extends JFrame {

    private BLFacade facade;
    private JTextArea resultadoArea;

    public RealizarPagoGUI(BLFacade facade) {
        this.facade = facade;

        setTitle("Pagar factura");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton cargarBtn = new JButton("Ver facturas");
        JButton pagarBtn = new JButton("Pagar (poner código)");
        JPanel topPanel = new JPanel();
        topPanel.add(cargarBtn);
        topPanel.add(pagarBtn);
        add(topPanel, BorderLayout.NORTH);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        cargarBtn.addActionListener((ActionEvent e) -> {
            List<Factura> facturas = facade.generarFacturas();
            resultadoArea.setText("");
            if (facturas.isEmpty()) {
                resultadoArea.setText("No hay facturas.");
            } else {
                for (Factura f : facturas) {
                    resultadoArea.append("Código: " + f.getCodigo() + " - Total: " + f.getTotal() + " €\n");
                }
            }
        });

        pagarBtn.addActionListener((ActionEvent e) -> {
            String codigo = JOptionPane.showInputDialog(this, "Pon el código de la factura:");
            if (codigo != null && !codigo.trim().isEmpty()) {
                boolean pagado = facade.pagar(codigo.trim());
                if (pagado) {
                    JOptionPane.showMessageDialog(this, "Pago hecho.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
