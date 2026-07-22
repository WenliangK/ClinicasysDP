package Vista;

import Componentes.SidebarButton;
import Singleton.GestorConfiguracion;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import ui.styles.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrincipalFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenido;

    private final List<SidebarButton> botonesMenu =
            new ArrayList<>();

    private DashboardCitasPanel dashboardPanel;
    private PacientesPanel pacientesPanel;
    private MedicosPanel medicosPanel;
    private NuevaCitaPanel nuevaCitaPanel;
    private FacturacionPanel facturacionPanel;
    private HistorialCitasPanel historialPanel;

    public PrincipalFrame() {
        String nombreClinica =
                GestorConfiguracion
                        .getInstancia()
                        .getNombreClinica();

        configurarVentana(nombreClinica);
        construirMenuLateral();
        construirHeader();
        construirAreaContenido();
    }

    private void configurarVentana(String nombreClinica) {
        setTitle(nombreClinica + " - Sistema de Gestión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setMinimumSize(
                new Dimension(
                        1080,
                        680
                )
        );

        setSize(
                1360,
                820
        );

        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(
                UIStyles.BACKGROUND
        );
    }

    private void construirHeader() {
        JPanel header =
                new JPanel(
                        new BorderLayout(
                                24,
                                0
                        )
                );

        header.setPreferredSize(
                new Dimension(
                        0,
                        96
                )
        );

        header.setBackground(
                UIStyles.CARD
        );

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                UIStyles.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                30,
                                15,
                                30
                        )
                )
        );

        header.add(
                crearPanelTitulos(),
                BorderLayout.CENTER
        );

        header.add(
                crearPanelUsuario(),
                BorderLayout.EAST
        );

        add(
                header,
                BorderLayout.NORTH
        );
    }

    private JPanel crearPanelTitulos() {
        JPanel panelTitulos =
                new JPanel();

        panelTitulos.setLayout(
                new BoxLayout(
                        panelTitulos,
                        BoxLayout.Y_AXIS
                )
        );

        panelTitulos.setOpaque(false);

        JLabel etiquetaSuperior =
                new JLabel(
                        "PANEL ADMINISTRATIVO"
                );

        etiquetaSuperior.setFont(
                UIStyles.SMALL
        );

        etiquetaSuperior.setForeground(
                UIStyles.PRIMARY
        );

        etiquetaSuperior.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel titulo =
                new JLabel(
                        "Sistema de Gestión Clínica"
                );

        titulo.setFont(
                UIStyles.TITLE
        );

        titulo.setForeground(
                UIStyles.TEXT
        );

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel subtitulo =
                new JLabel(
                        "Gestiona citas, pacientes, médicos y facturación desde un único lugar."
                );

        subtitulo.setFont(
                UIStyles.NORMAL
        );

        subtitulo.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        subtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelTitulos.add(
                Box.createVerticalGlue()
        );

        panelTitulos.add(
                etiquetaSuperior
        );

        panelTitulos.add(
                Box.createVerticalStrut(3)
        );

        panelTitulos.add(
                titulo
        );

        panelTitulos.add(
                Box.createVerticalStrut(3)
        );

        panelTitulos.add(
                subtitulo
        );

        panelTitulos.add(
                Box.createVerticalGlue()
        );

        return panelTitulos;
    }

    private JPanel crearPanelUsuario() {
        JPanel contenedor =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        contenedor.setOpaque(false);

        contenedor.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyles.BORDER,
                                1,
                                true
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                12,
                                7,
                                14
                        )
                )
        );

        JLabel avatar =
                new JLabel(
                        "A",
                        SwingConstants.CENTER
                );

        avatar.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        avatar.setForeground(
                Color.WHITE
        );

        avatar.setBackground(
                UIStyles.PRIMARY
        );

        avatar.setOpaque(true);

        avatar.setPreferredSize(
                new Dimension(
                        44,
                        44
                )
        );

        JPanel panelDatos =
                new JPanel();

        panelDatos.setLayout(
                new BoxLayout(
                        panelDatos,
                        BoxLayout.Y_AXIS
                )
        );

        panelDatos.setOpaque(false);

        JLabel nombreUsuario =
                new JLabel(
                        "Administrador"
                );

        nombreUsuario.setFont(
                UIStyles.BUTTON
        );

        nombreUsuario.setForeground(
                UIStyles.TEXT
        );

        nombreUsuario.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel rolUsuario =
                new JLabel(
                        "Administrador del sistema"
                );

        rolUsuario.setFont(
                UIStyles.SMALL
        );

        rolUsuario.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        rolUsuario.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelDatos.add(
                Box.createVerticalGlue()
        );

        panelDatos.add(
                nombreUsuario
        );

        panelDatos.add(
                Box.createVerticalStrut(3)
        );

        panelDatos.add(
                rolUsuario
        );

        panelDatos.add(
                Box.createVerticalGlue()
        );

        contenedor.add(
                avatar,
                BorderLayout.WEST
        );

        contenedor.add(
                panelDatos,
                BorderLayout.CENTER
        );

        return contenedor;
    }

    private void construirMenuLateral() {
        JPanel menu =
                new JPanel() {
                    @Override
                    protected void paintComponent(
                            Graphics graphics
                    ) {
                        Graphics2D g2 =
                                (Graphics2D) graphics.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY
                        );

                        GradientPaint degradado =
                                new GradientPaint(
                                        0,
                                        0,
                                        UIStyles.SIDEBAR,
                                        0,
                                        getHeight(),
                                        new Color(
                                                12,
                                                23,
                                                41
                                        )
                                );

                        g2.setPaint(degradado);
                        g2.fillRect(
                                0,
                                0,
                                getWidth(),
                                getHeight()
                        );

                        g2.dispose();
                    }
                };

        menu.setLayout(
                new BoxLayout(
                        menu,
                        BoxLayout.Y_AXIS
                )
        );

        menu.setPreferredSize(
                new Dimension(
                        280,
                        0
                )
        );

        menu.setOpaque(false);

        menu.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        18,
                        22,
                        18
                )
        );

        menu.add(
                crearLogo()
        );

        menu.add(
                Box.createVerticalStrut(22)
        );

        menu.add(
                crearEtiquetaSeccion(
                        "GESTIÓN PRINCIPAL"
                )
        );

        menu.add(
                Box.createVerticalStrut(10)
        );

        agregarBotonMenu(
                menu,
                "Dashboard de citas",
                "DASHBOARD",
                "/icons/layout-dashboard.svg"
        );

        agregarBotonMenu(
                menu,
                "Registrar nueva cita",
                "NUEVA_CITA",
                "/icons/calendar-event.svg"
        );

        agregarBotonMenu(
                menu,
                "Historial de citas",
                "HISTORIAL",
                "/icons/history.svg"
        );

        menu.add(
                Box.createVerticalStrut(14)
        );

        menu.add(
                crearEtiquetaSeccion(
                        "ADMINISTRACIÓN"
                )
        );

        menu.add(
                Box.createVerticalStrut(10)
        );

        agregarBotonMenu(
                menu,
                "Pacientes",
                "PACIENTES",
                "/icons/users.svg"
        );

        agregarBotonMenu(
                menu,
                "Médicos",
                "MEDICOS",
                "/icons/stethoscope.svg"
        );

        agregarBotonMenu(
                menu,
                "Facturación",
                "FACTURACION",
                "/icons/file-invoice.svg"
        );

        menu.add(
                Box.createVerticalGlue()
        );

        menu.add(
                crearPieMenu()
        );

        add(
                menu,
                BorderLayout.WEST
        );
    }

    private JPanel crearLogo() {
        JPanel panelLogo =
                new JPanel(
                        new BorderLayout(
                                13,
                                0
                        )
                );

        panelLogo.setOpaque(false);

        panelLogo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        68
                )
        );

        panelLogo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel icono =
                new JLabel(
                        String.valueOf(SwingConstants.CENTER)
                );

        Icon iconoHospital =
                cargarIcono(
                        "/icons/hospital.svg",
                        31,
                        31
                );

        if (iconoHospital != null) {
            icono.setIcon(
                    iconoHospital
            );
        } else {
            icono.setText(
                    "✚"
            );

            icono.setFont(
                    new Font(
                            "Segoe UI Symbol",
                            Font.BOLD,
                            28
                    )
            );

            icono.setForeground(
                    Color.WHITE
            );
        }

        icono.setBackground(
                new Color(
                        37,
                        99,
                        235
                )
        );

        icono.setOpaque(true);

        icono.setPreferredSize(
                new Dimension(
                        54,
                        54
                )
        );

        icono.putClientProperty(
                "JComponent.arc",
                16
        );

        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setOpaque(false);

        JLabel nombre =
                new JLabel(
                        "CLINICASYS"
                );

        nombre.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        21
                )
        );

        nombre.setForeground(
                Color.WHITE
        );

        nombre.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel descripcion =
                new JLabel(
                        "Sistema Médico"
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        descripcion.setForeground(
                new Color(
                        205,
                        218,
                        232
                )
        );

        descripcion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        textos.add(
                Box.createVerticalGlue()
        );

        textos.add(
                nombre
        );

        textos.add(
                Box.createVerticalStrut(3)
        );

        textos.add(
                descripcion
        );

        textos.add(
                Box.createVerticalGlue()
        );

        panelLogo.add(
                icono,
                BorderLayout.WEST
        );

        panelLogo.add(
                textos,
                BorderLayout.CENTER
        );

        return panelLogo;
    }

    private JLabel crearEtiquetaSeccion(
            String texto
    ) {
        JLabel etiqueta =
                new JLabel(
                        texto
                );

        etiqueta.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );

        etiqueta.setForeground(
                new Color(
                        143,
                        165,
                        190
                )
        );

        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        12,
                        0,
                        0
                )
        );

        etiqueta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        etiqueta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        24
                )
        );

        return etiqueta;
    }

    private JPanel crearPieMenu() {
        JPanel pie =
                new JPanel();

        pie.setLayout(
                new BoxLayout(
                        pie,
                        BoxLayout.Y_AXIS
                )
        );

        pie.setOpaque(false);

        pie.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        pie.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        70
                )
        );

        pie.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                new Color(
                                        255,
                                        255,
                                        255,
                                        30
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                14,
                                12,
                                0,
                                12
                        )
                )
        );

        JLabel estado =
                new JLabel(
                        "●  Sistema conectado"
                );

        estado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        estado.setForeground(
                new Color(
                        103,
                        232,
                        174
                )
        );

        estado.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel version =
                new JLabel(
                        "ClínicaSys · Panel administrativo"
                );

        version.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );

        version.setForeground(
                new Color(
                        167,
                        185,
                        204
                )
        );

        version.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        pie.add(
                estado
        );

        pie.add(
                Box.createVerticalStrut(6)
        );

        pie.add(
                version
        );

        return pie;
    }

    private void agregarBotonMenu(
            JPanel menu,
            String texto,
            String cardName,
            String rutaIcono
    ) {
        SidebarButton btn =
                new SidebarButton(
                        texto,
                        cargarIcono(
                                rutaIcono,
                                20,
                                20
                        )
                );

        boolean esDashboard =
                cardName.equals(
                        "DASHBOARD"
                );

        btn.setActivo(
                esDashboard
        );

        btn.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        btn.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        50
                )
        );

        botonesMenu.add(
                btn
        );

        btn.addActionListener(e -> {
            actualizarBotonActivo(
                    btn
            );

            cardLayout.show(
                    panelContenido,
                    cardName
            );

            if (cardName.equals(
                    "DASHBOARD"
            )) {
                dashboardPanel.cargarDatos();
            }

            if (cardName.equals(
                    "PACIENTES"
            )) {
                pacientesPanel.cargarTabla();
            }

            if (cardName.equals(
                    "MEDICOS"
            )) {
                medicosPanel.cargarTabla();
            }

            if (cardName.equals(
                    "HISTORIAL"
            )) {
                historialPanel.cargarDatos();
            }

            if (cardName.equals(
                    "NUEVA_CITA"
            )) {
                nuevaCitaPanel.cargarPacientes();
                nuevaCitaPanel.cargarMedicos();
            }

            if (cardName.equals(
                    "FACTURACION"
            )) {
                facturacionPanel.cargarPacientes();
            }
        });

        menu.add(
                btn
        );

        menu.add(
                Box.createVerticalStrut(7)
        );
    }

    private Icon cargarIcono(
            String ruta,
            int ancho,
            int alto
    ) {
        try {
            if (
                    getClass().getResource(
                            ruta
                    ) == null
            ) {
                System.err.println(
                        "No se encontró el icono: "
                                + ruta
                );

                return null;
            }

            return new FlatSVGIcon(
                    ruta,
                    ancho,
                    alto
            );
        } catch (Exception ex) {
            System.err.println(
                    "No se pudo cargar el icono: "
                            + ruta
            );

            return null;
        }
    }

    private void actualizarBotonActivo(
            SidebarButton botonSeleccionado
    ) {
        for (
                SidebarButton boton
                : botonesMenu
        ) {
            boton.setActivo(
                    boton
                            == botonSeleccionado
            );
        }
    }

    private void construirAreaContenido() {
        cardLayout =
                new CardLayout();

        panelContenido =
                new JPanel(
                        cardLayout
                );

        panelContenido.setBackground(
                UIStyles.BACKGROUND
        );

        JPanel contenedor =
                new JPanel(
                        new BorderLayout()
                );

        contenedor.setBackground(
                UIStyles.BACKGROUND
        );

        dashboardPanel =
                new DashboardCitasPanel();

        pacientesPanel =
                new PacientesPanel();

        medicosPanel =
                new MedicosPanel();

        nuevaCitaPanel =
                new NuevaCitaPanel();

        facturacionPanel =
                new FacturacionPanel();

        historialPanel =
                new HistorialCitasPanel();

        dashboardPanel.setOnCitaFinalizada(() -> {
            historialPanel.cargarDatos();

            cardLayout.show(
                    panelContenido,
                    "HISTORIAL"
            );
        });

        panelContenido.add(
                dashboardPanel,
                "DASHBOARD"
        );

        panelContenido.add(
                nuevaCitaPanel,
                "NUEVA_CITA"
        );

        panelContenido.add(
                historialPanel,
                "HISTORIAL"
        );

        panelContenido.add(
                pacientesPanel,
                "PACIENTES"
        );

        panelContenido.add(
                medicosPanel,
                "MEDICOS"
        );

        panelContenido.add(
                facturacionPanel,
                "FACTURACION"
        );

        contenedor.add(
                panelContenido,
                BorderLayout.CENTER
        );

        add(
                contenedor,
                BorderLayout.CENTER
        );
    }
}