package Vista;
import Singleton.GestorConfiguracion;


import javax.swing.*;
import java.awt.*;
public class PrincipalFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenido;

    private DashboardCitasPanel dashboardPanel;
    private PacientesPanel pacientesPanel;
    private MedicosPanel medicosPanel;
    private NuevaCitaPanel nuevaCitaPanel;
    private FacturacionPanel facturacionPanel;
    private HistorialCitasPanel historialPanel;

    public PrincipalFrame() {
        String nombreClinica = GestorConfiguracion.getInstancia().getNombreClinica();
        setTitle(nombreClinica + " - Sistema de Gestion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        construirMenuLateral();
        construirHeader();
        construirAreaContenido();
        
    }

    private void construirHeader(){

        JPanel header = new JPanel(new BorderLayout());

        header.setPreferredSize(new Dimension(0,65));

        header.setBackground(Color.WHITE);

        header.setBorder(BorderFactory.createEmptyBorder(10,25,10,25));

        JLabel titulo = new JLabel("Sistema de Gestión Clínica");

        titulo.setFont(new Font("Segoe UI",Font.BOLD,24));

        JLabel usuario = new JLabel("Administrador");

        usuario.setFont(new Font("Segoe UI",Font.PLAIN,15));

        header.add(titulo,BorderLayout.WEST);

        header.add(usuario,BorderLayout.EAST);

        add(header,BorderLayout.NORTH);

    }

    private void construirMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(240, 0));
        menu.setBackground(new Color(18, 38, 63));
        menu.setBorder(BorderFactory.createEmptyBorder(25,20,25,20));

        JLabel lblLogo = new JLabel(
                "<html><center><span style='font-size:18px;'>🏥</span><br><b>CLINICASYS</b></center></html>");

        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20,0,30,0));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        menu.add(lblLogo);

        agregarBotonMenu(menu, "Dashboard Citas",  "DASHBOARD");
        agregarBotonMenu(menu, "Nueva Cita",       "NUEVA_CITA");
        agregarBotonMenu(menu, "Historial Citas",  "HISTORIAL");
        agregarBotonMenu(menu, "Pacientes",        "PACIENTES");
        agregarBotonMenu(menu, "Medicos",          "MEDICOS");
        agregarBotonMenu(menu, "Facturacion",      "FACTURACION");

        add(menu, BorderLayout.WEST);
    }

    private void agregarBotonMenu(JPanel menu, String texto, String cardName) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        btn.setForeground(Color.WHITE);

        btn.setBackground(new Color(18,38,63));

        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createEmptyBorder(12,15,12,15));

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));

        btn.putClientProperty("JButton.buttonType","roundRect");
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(e -> {
            cardLayout.show(panelContenido, cardName);
            if (cardName.equals("DASHBOARD"))    dashboardPanel.cargarDatos();
            if (cardName.equals("PACIENTES"))    pacientesPanel.cargarTabla();
            if (cardName.equals("MEDICOS"))      medicosPanel.cargarTabla();
            if (cardName.equals("HISTORIAL"))    historialPanel.cargarDatos();
            if (cardName.equals("NUEVA_CITA")) { nuevaCitaPanel.cargarPacientes(); nuevaCitaPanel.cargarMedicos(); }
            if (cardName.equals("FACTURACION"))  facturacionPanel.cargarPacientes();
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {

                btn.setBackground(new Color(33,150,243));

            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {

                btn.setBackground(new Color(18,38,63));

            }

        });
        menu.add(btn);
        menu.add(Box.createVerticalStrut(12));
    }


    private void construirAreaContenido() {
        cardLayout = new CardLayout();

        panelContenido = new JPanel(cardLayout);

        panelContenido.setBackground(new Color(245,247,250));

        JPanel contenedor = new JPanel(new BorderLayout());

        contenedor.setBackground(new Color(245,247,250));

        contenedor.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        contenedor.add(panelContenido, BorderLayout.CENTER);

        dashboardPanel   = new DashboardCitasPanel();
        pacientesPanel   = new PacientesPanel();
        medicosPanel     = new MedicosPanel();
        nuevaCitaPanel   = new NuevaCitaPanel();
        facturacionPanel = new FacturacionPanel();
        historialPanel   = new HistorialCitasPanel();

        dashboardPanel.setOnCitaFinalizada(() -> {
            historialPanel.cargarDatos();
            cardLayout.show(panelContenido, "HISTORIAL");
        });

        panelContenido.add(dashboardPanel,   "DASHBOARD");
        panelContenido.add(nuevaCitaPanel,   "NUEVA_CITA");
        panelContenido.add(historialPanel,   "HISTORIAL");
        panelContenido.add(pacientesPanel,   "PACIENTES");
        panelContenido.add(medicosPanel,     "MEDICOS");
        panelContenido.add(facturacionPanel, "FACTURACION");

        add(contenedor, BorderLayout.CENTER);
    }
}