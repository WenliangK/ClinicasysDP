package Vista;

import Componentes.DashboardCard;
import Componentes.ModernButton;
import Componentes.ModernScrollPane;
import Componentes.ModernTable;
import Componentes.SectionHeader;
import Componentes.StatusBadge;
import Controlador.GestorCitas;
import Modelo.Cita;
import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class HistorialCitasPanel extends JPanel {

    private ModernTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;
    private ModernButton btnRefrescar;


    public HistorialCitasPanel() {
        configurarPanel();
        inicializarComponentes();
        cargarDatos();
    }



    private void configurarPanel() {
        setLayout(
                new BorderLayout()
        );

        setBackground(
                UIStyles.BACKGROUND
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        0,
                        0
                )
        );
    }



    private void inicializarComponentes() {
        JPanel contenido =
                new JPanel();

        contenido.setOpaque(false);

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        contenido.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        24,
                        24,
                        24
                )
        );

        SectionHeader encabezado =
                crearEncabezado();

        DashboardCard tarjetaHistorial =
                crearTarjetaHistorial();

        encabezado.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjetaHistorial.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        encabezado.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        encabezado
                                .getPreferredSize()
                                .height
                )
        );

        tarjetaHistorial.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );

        contenido.add(
                encabezado
        );

        contenido.add(
                Box.createVerticalStrut(
                        18
                )
        );

        contenido.add(
                tarjetaHistorial
        );


        JScrollPane scrollPrincipal =
                new JScrollPane(
                        contenido
                );

        scrollPrincipal.setBorder(
                null
        );

        scrollPrincipal.setOpaque(
                false
        );

        scrollPrincipal
                .getViewport()
                .setOpaque(false);

        scrollPrincipal.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPrincipal.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPrincipal
                .getVerticalScrollBar()
                .setUnitIncrement(18);

        add(
                scrollPrincipal,
                BorderLayout.CENTER
        );
    }



    private SectionHeader crearEncabezado() {
        SectionHeader encabezado =
                new SectionHeader(
                        "Historial de citas",
                        "Consulta todas las citas registradas y revisa su estado actual.",
                        UIStyles.HISTORIAL_ACCENT,
                        "◷"
                );

        StatusBadge badge =
                new StatusBadge(
                        "HISTORIAL"
                );

        encabezado.setRightComponent(
                badge
        );

        return encabezado;
    }



    private DashboardCard crearTarjetaHistorial() {
        DashboardCard tarjeta =
                new DashboardCard(
                        "Registro de atenciones",
                        "Visualiza las citas registradas y su último estado.",
                        UIStyles.HISTORIAL_ACCENT,
                        "▤"
                );

        JPanel contenidoTarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                16
                        )
                );

        contenidoTarjeta.setOpaque(
                false
        );

        contenidoTarjeta.add(
                crearPanelInformativo(),
                BorderLayout.NORTH
        );

        contenidoTarjeta.add(
                crearTabla(),
                BorderLayout.CENTER
        );

        contenidoTarjeta.add(
                crearPanelInferior(),
                BorderLayout.SOUTH
        );

        tarjeta.setContenido(
                contenidoTarjeta
        );

        tarjeta.setPreferredSize(
                new Dimension(
                        0,
                        590
                )
        );

        return tarjeta;
    }



    private JPanel crearPanelInformativo() {
        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        JLabel ayuda =
                new JLabel(
                        "Las citas más recientes se muestran según el orden recibido."
                );

        ayuda.setFont(
                UIStyles.SMALL
        );

        ayuda.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        JLabel indicacion =
                new JLabel(
                        "Puedes ordenar haciendo clic en una columna"
                );

        indicacion.setFont(
                UIStyles.SMALL
        );

        indicacion.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        indicacion.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        panel.add(
                ayuda,
                BorderLayout.WEST
        );

        panel.add(
                indicacion,
                BorderLayout.EAST
        );

        return panel;
    }



    private ModernScrollPane crearTabla() {
        String[] columnas = {
                "ID",
                "Paciente",
                "Médico",
                "Fecha y hora",
                "Estado"
        };

        modeloTabla =
                new DefaultTableModel(
                        columnas,
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int fila,
                            int columna
                    ) {
                        return false;
                    }
                };

        tablaHistorial =
                new ModernTable(
                        modeloTabla
                );

        tablaHistorial.setRowHeight(
                48
        );

        tablaHistorial.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaHistorial.setAutoCreateRowSorter(
                true
        );

        tablaHistorial.setShowVerticalLines(
                false
        );

        tablaHistorial.setShowHorizontalLines(
                true
        );

        tablaHistorial.setGridColor(
                UIStyles.BORDER
        );

        tablaHistorial.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        tablaHistorial
                .getTableHeader()
                .setReorderingAllowed(false);

        tablaHistorial
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                44
                        )
                );

        configurarColumnas();
        configurarRenderizadores();

        ModernScrollPane scrollPane =
                new ModernScrollPane(
                        tablaHistorial
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        4,
                        0,
                        0,
                        0
                )
        );

        scrollPane.setPreferredSize(
                new Dimension(
                        0,
                        410
                )
        );

        return scrollPane;
    }



    private void configurarColumnas() {
        tablaHistorial
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(60);

        tablaHistorial
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(85);

        tablaHistorial
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(210);

        tablaHistorial
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(210);

        tablaHistorial
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(180);

        tablaHistorial
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(155);
    }



    private void configurarRenderizadores() {
        DefaultTableCellRenderer renderCentrado =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable tabla,
                            Object valor,
                            boolean seleccionado,
                            boolean tieneFoco,
                            int fila,
                            int columna
                    ) {
                        Component componente =
                                super.getTableCellRendererComponent(
                                        tabla,
                                        valor,
                                        seleccionado,
                                        tieneFoco,
                                        fila,
                                        columna
                                );

                        setHorizontalAlignment(
                                SwingConstants.CENTER
                        );

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0,
                                        10,
                                        0,
                                        10
                                )
                        );

                        if (!seleccionado) {
                            componente.setBackground(
                                    fila % 2 == 0
                                            ? UIStyles.CARD_BACKGROUND
                                            : UIStyles.SOFT_BACKGROUND
                            );

                            componente.setForeground(
                                    UIStyles.TEXT
                            );
                        }

                        return componente;
                    }
                };

        tablaHistorial
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        renderCentrado
                );

        tablaHistorial
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        renderCentrado
                );

        tablaHistorial
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        crearRenderizadorEstado()
                );
    }

    private TableCellRenderer crearRenderizadorEstado() {
        return new TableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable tabla,
                    Object valor,
                    boolean seleccionado,
                    boolean tieneFoco,
                    int fila,
                    int columna
            ) {
                String estado =
                        valor == null
                                ? ""
                                : valor.toString();

                StatusBadge badge =
                        new StatusBadge(
                                estado
                        );

                JPanel contenedor =
                        new JPanel(
                                new GridBagLayout()
                        );

                contenedor.setOpaque(
                        true
                );

                if (seleccionado) {
                    contenedor.setBackground(
                            tabla.getSelectionBackground()
                    );
                } else {
                    contenedor.setBackground(
                            fila % 2 == 0
                                    ? UIStyles.CARD_BACKGROUND
                                    : UIStyles.SOFT_BACKGROUND
                    );
                }

                contenedor.add(
                        badge
                );

                return contenedor;
            }
        };
    }



    private JPanel crearPanelInferior() {
        JPanel panelInferior =
                new JPanel(
                        new BorderLayout(
                                16,
                                0
                        )
                );

        panelInferior.setOpaque(
                false
        );

        panelInferior.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        0,
                        0,
                        0
                )
        );

        JPanel panelBoton =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        panelBoton.setOpaque(
                false
        );

        btnRefrescar =
                new ModernButton(
                        "Refrescar historial",
                        ModernButton.Tipo.SECUNDARIO
                );

        btnRefrescar.setToolTipText(
                "Volver a cargar todas las citas registradas"
        );


        btnRefrescar.addActionListener(
                e -> cargarDatos()
        );

        panelBoton.add(
                btnRefrescar
        );

        lblEstado =
                new JLabel(
                        "Preparando historial..."
                );

        lblEstado.setFont(
                UIStyles.SMALL
        );

        lblEstado.setForeground(
                UIStyles.TEXT_SECONDARY
        );

        lblEstado.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        panelInferior.add(
                panelBoton,
                BorderLayout.WEST
        );

        panelInferior.add(
                lblEstado,
                BorderLayout.CENTER
        );

        return panelInferior;
    }



    public void cargarDatos() {
        lblEstado.setText(
                "Cargando historial..."
        );

        btnRefrescar.setEnabled(
                false
        );

        btnRefrescar.setText(
                "Cargando..."
        );

        GestorCitas
                .getInstancia()
                .getTodas()
                .thenAccept(historial ->
                        SwingUtilities.invokeLater(() -> {
                            modeloTabla.setRowCount(
                                    0
                            );

                            for (Cita cita : historial) {
                                modeloTabla.addRow(
                                        new Object[]{
                                                cita.getId(),

                                                cita.getPaciente() != null
                                                        ? cita
                                                        .getPaciente()
                                                        .getNombre()
                                                        : "N/A",

                                                cita.getMedico(),

                                                cita.getFechaHora(),

                                                cita.getEstado()
                                        }
                                );
                            }

                            lblEstado.setText(
                                    historial.size()
                                            + " cita(s) registrada(s)"
                            );

                            btnRefrescar.setEnabled(
                                    true
                            );

                            btnRefrescar.setText(
                                    "Refrescar historial"
                            );
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "No se pudo cargar el historial."
                        );

                        btnRefrescar.setEnabled(
                                true
                        );

                        btnRefrescar.setText(
                                "Refrescar historial"
                        );

                        JOptionPane.showMessageDialog(
                                this,
                                "Error de red: "
                                        + error.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    });

                    return null;
                });
    }
}