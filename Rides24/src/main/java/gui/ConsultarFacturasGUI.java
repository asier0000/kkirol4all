	package gui;
	
	import businessLogic.*;
	import domain.*;
	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.ActionEvent;
	import java.util.List;
	
	public class ConsultarFacturasGUI extends JFrame {
	
	    private BLFacade facade;
	    private Socio socio;
	    private JTextArea resultadoArea;
	    private List<Factura> facturasPendientes;
	
	    public ConsultarFacturasGUI(BLFacade facade, Socio socio) {
	        this.facade = facade;
	        this.socio = socio;
	
	        setTitle("Ver y pagar facturas");
	        setSize(600, 450);
	        setLocationRelativeTo(null);
	        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	        setLayout(new BorderLayout());
	
	        JPanel topPanel = new JPanel();
	        JButton cargarBtn = new JButton("Cargar facturas");
	        JButton pagarBtn = new JButton("Pagar (por código)");
	
	        topPanel.add(cargarBtn);
	        topPanel.add(pagarBtn);
	        add(topPanel, BorderLayout.NORTH);
	
	        resultadoArea = new JTextArea();
	        resultadoArea.setEditable(false);
	        add(new JScrollPane(resultadoArea), BorderLayout.CENTER);
	
	        cargarBtn.addActionListener((ActionEvent e) -> {
	            facturasPendientes = facade.verFacturas(socio);
	            resultadoArea.setText("");
	
	            if (facturasPendientes.isEmpty()) {
	                resultadoArea.setText("No tienes facturas pendientes.");
	            } else {
	                for (Factura f : facturasPendientes) {
	                    resultadoArea.append("Código: " + f.getCodigo() + "\n");
	                    resultadoArea.append("Fecha: " + f.getFecha() + "\n");
	                    resultadoArea.append("Total: " + f.getTotal() + " €\n");
	
	                    if (f.getReservas() != null && !f.getReservas().isEmpty()) {
	                        resultadoArea.append("Reservas:\n");
	                        for (Reserva r : f.getReservas()) {
	                            resultadoArea.append("- " + r.getSesion().getActividad().getNombre() +
	                                    " en " + r.getSesion().getSala().getNombre() +
	                                    " el " + r.getSesion().getFechaHora() + "\n");
	                        }
	                    }
	
	                    resultadoArea.append("------\n");
	                }
	            }
	        });
	
	        pagarBtn.addActionListener((ActionEvent e) -> {
	            String codigo = JOptionPane.showInputDialog(this, "Pon el código de la factura a pagar:");
	            if (codigo != null && !codigo.isBlank()) {
	                boolean ok = facade.pagar(codigo.trim());
	                if (ok) {
	                    JOptionPane.showMessageDialog(this, "Factura pagada.");
	                } else {
	                    JOptionPane.showMessageDialog(this, "No se pudo pagar.", "Error", JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        });
	    }
	}
