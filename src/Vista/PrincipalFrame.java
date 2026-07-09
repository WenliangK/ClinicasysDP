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
        construirAreaContenido();
    }

    private void construirMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(200, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        JLabel lblLogo = new JLabel("<html><b>Clinica<br>San Rafael</b></html>");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 16));
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
        menu.add(btn);
        menu.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    private void construirAreaContenido() {
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        dashboardPanel   = new DashboardCitasPanel();
        pacientesPanel   = new PacientesPanel();
        medicosPanel     = new MedicosPanel();
        nuevaCitaPanel   = new NuevaCitaPanel();
        facturacionPanel = new FacturacionPanel();
        historialPanel   = new HistorialCitasPanel();

        // Cuando una cita del dashboard se marca ATENDIDO o CANCELADO,
        // se refresca el historial y se navega automaticamente hacia el.
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

        add(panelContenido, BorderLayout.CENTER);
    }
}