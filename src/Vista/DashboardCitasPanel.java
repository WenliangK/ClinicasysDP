package Vista;

import Componentes.CitasChartPanel;
import Componentes.DashboardCard;
import Componentes.DashboardStatsPanel;
import Componentes.ModernButton;
import Componentes.ModernScrollPane;
import Componentes.ModernTable;
import Componentes.SectionHeader;
import Componentes.StatCard;
import Componentes.StatusBadge;
import Controlador.GestorCitas;
import Modelo.Cita;
import Observer.Observador;
import Utilidades.RespuestaHttp;
import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DashboardCitasPanel
        extends JPanel
        implements Observador {

    private ModernTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;
    private Runnable onCitaFinalizada;


    private StatCard cardTotalCitas;
    private StatCard cardProgramadas;
    private StatCard cardConsultorio;
    private StatCard cardSeleccionada;

    private CitasChartPanel graficoCitas;

    public DashboardCitasPanel() {
        configurarPanel();
        inicializarComponentes();


        GestorCitas
                .getInstancia()
                .suscribir(this);


        iniciarAutoRefresh();
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

        DashboardStatsPanel estadisticas =
                crearPanelEstadisticas();

        DashboardCard tarjetaGrafico =
                crearTarjetaGrafico();

        DashboardCard tarjetaTabla =
                crearTarjetaTabla();

        encabezado.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        estadisticas.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjetaGrafico.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjetaTabla.setAlignmentX(
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

        estadisticas.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        122
                )
        );

        tarjetaGrafico.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        255
                )
        );

        tarjetaTabla.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        480
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
                estadisticas
        );

        contenido.add(
                Box.createVerticalStrut(
                        20
                )
        );

        contenido.add(
                tarjetaGrafico
        );

        contenido.add(
                Box.createVerticalStrut(
                        20
                )
        );

        contenido.add(
                tarjetaTabla
        );

        contenido.add(
                Box.createVerticalStrut(
                        10
                )
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
                        "Dashboard de Citas",
                        "Consulta, supervisa y administra las citas vigentes.",
                        UIStyles.DASHBOARD_ACCENT,
                        "⌂"
                );

        StatusBadge badge =
                new StatusBadge(
                        "ACTIVO"
                );

        encabezado.setRightComponent(
                badge
        );

        return encabezado;
    }



    private DashboardStatsPanel crearPanelEstadisticas() {
        DashboardStatsPanel panelEstadisticas =
                new DashboardStatsPanel();

        cardTotalCitas =
                new StatCard(
                        "Citas vigentes",
                        "0",
                        "Total de citas registradas",
                        UIStyles.DASHBOARD_ACCENT,
                        "✚"
                );

        cardProgramadas =
                new StatCard(
                        "Programadas",
                        "0",
                        "Esperando atención",
                        UIStyles.CITAS_ACCENT,
                        "◷"
                );

        cardConsultorio =
                new StatCard(
                        "En consultorio",
                        "0",
                        "Actualmente en atención",
                        UIStyles.HISTORIAL_ACCENT,
                        "⚕"
                );

        cardSeleccionada =
                new StatCard(
                        "Cita seleccionada",
                        "Ninguna",
                        "Selecciona una fila",
                        UIStyles.FACTURACION_ACCENT,
                        "✓"
                );

        panelEstadisticas.agregarStatCard(
                cardTotalCitas
        );

        panelEstadisticas.agregarStatCard(
                cardProgramadas
        );

        panelEstadisticas.agregarStatCard(
                cardConsultorio
        );

        panelEstadisticas.agregarStatCard(
                cardSeleccionada
        );

        panelEstadisticas.setPreferredSize(
                new Dimension(
                        0,
                        122
                )
        );

        return panelEstadisticas;
    }



    private DashboardCard crearTarjetaGrafico() {
        DashboardCard tarjetaGrafico =
                new DashboardCard(
                        "Distribución de citas",
                        "Resumen visual de las citas vigentes según su estado.",
                        UIStyles.CITAS_ACCENT,
                        "▥"
                );

        graficoCitas =
                new CitasChartPanel();

        tarjetaGrafico.setContenido(
                graficoCitas
        );

        tarjetaGrafico.setPreferredSize(
                new Dimension(
                        0,
                        255
                )
        );

        return tarjetaGrafico;
    }



    private DashboardCard crearTarjetaTabla() {
        DashboardCard tarjetaTabla =
                new DashboardCard(
                        "Listado de citas programadas",
                        "Selecciona una cita para modificar su estado.",
                        UIStyles.DASHBOARD_ACCENT,
                        "▤"
                );

        JPanel contenidoTarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                16
                        )
                );

        contenidoTarjeta.setOpaque(false);

        contenidoTarjeta.add(
                crearTabla(),
                BorderLayout.CENTER
        );

        contenidoTarjeta.add(
                crearPanelAcciones(),
                BorderLayout.SOUTH
        );

        tarjetaTabla.setContenido(
                contenidoTarjeta
        );

        tarjetaTabla.setPreferredSize(
                new Dimension(
                        0,
                        480
                )
        );

        return tarjetaTabla;
    }



    private ModernScrollPane crearTabla() {
        String[] columnas = {
                "ID",
                "Paciente",
                "Médico",
                "Fecha y hora",
                "Motivo",
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

        tablaCitas =
                new ModernTable(
                        modeloTabla
                );

        tablaCitas.setRowHeight(
                48
        );

        tablaCitas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaCitas.setShowVerticalLines(
                false
        );

        tablaCitas.setShowHorizontalLines(
                true
        );

        tablaCitas.setGridColor(
                UIStyles.BORDER
        );

        tablaCitas.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        tablaCitas
                .getTableHeader()
                .setReorderingAllowed(false);

        tablaCitas
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                44
                        )
                );

        /*
         * Este listener únicamente actualiza
         * la tarjeta visual de cita seleccionada.
         */
        tablaCitas
                .getSelectionModel()
                .addListSelectionListener(evento -> {
                    if (!evento.getValueIsAdjusting()) {
                        actualizarTarjetaSeleccionada();
                    }
                });

        configurarColumnas();
        configurarRenderizadores();

        ModernScrollPane scrollPane =
                new ModernScrollPane(
                        tablaCitas
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        0,
                        0,
                        0
                )
        );

        return scrollPane;
    }

    private void configurarColumnas() {
        tablaCitas
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        tablaCitas
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(80);

        tablaCitas
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(180);

        tablaCitas
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(170);

        tablaCitas
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(155);

        tablaCitas
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(230);

        tablaCitas
                .getColumnModel()
                .getColumn(5)
                .setPreferredWidth(145);
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
                                        8,
                                        0,
                                        8
                                )
                        );

                        /*
                         * Filas alternadas.
                         */
                        if (!seleccionado) {
                            componente.setBackground(
                                    fila % 2 == 0
                                            ? UIStyles.CARD_BACKGROUND
                                            : UIStyles.SOFT_BACKGROUND
                            );
                        }

                        return componente;
                    }
                };

        tablaCitas
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        renderCentrado
                );

        tablaCitas
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        renderCentrado
                );

        tablaCitas
                .getColumnModel()
                .getColumn(5)
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

                contenedor.setOpaque(true);

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



    private void actualizarTarjetaSeleccionada() {
        int filaVista =
                tablaCitas.getSelectedRow();

        if (filaVista == -1) {
            cardSeleccionada.setValor(
                    "Ninguna"
            );

            cardSeleccionada.setDescripcion(
                    "Selecciona una fila"
            );

            return;
        }

        int filaModelo =
                tablaCitas.convertRowIndexToModel(
                        filaVista
                );

        Object id =
                modeloTabla.getValueAt(
                        filaModelo,
                        0
                );

        Object estado =
                modeloTabla.getValueAt(
                        filaModelo,
                        5
                );

        cardSeleccionada.setValor(
                "Cita #" + id
        );

        cardSeleccionada.setDescripcion(
                "Estado: "
                        + formatearEstado(
                        estado
                )
        );
    }

    private String formatearEstado(
            Object estado
    ) {
        if (estado == null) {
            return "Sin estado";
        }

        return estado
                .toString()
                .replace(
                        "_",
                        " "
                );
    }



    private JPanel crearPanelAcciones() {
        JPanel panelAcciones =
                new JPanel(
                        new BorderLayout(
                                15,
                                0
                        )
                );

        panelAcciones.setOpaque(false);

        panelAcciones.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        0,
                        0,
                        0
                )
        );

        JPanel botones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        botones.setOpaque(false);

        ModernButton btnConsultorio =
                new ModernButton(
                        "En consultorio",
                        ModernButton.Tipo.ADVERTENCIA
                );

        ModernButton btnAtendido =
                new ModernButton(
                        "Marcar atendido",
                        ModernButton.Tipo.PRIMARIO
                );

        ModernButton btnCancelar =
                new ModernButton(
                        "Cancelar cita",
                        ModernButton.Tipo.PELIGRO
                );

        btnConsultorio.setToolTipText(
                "Cambiar la cita al estado En consultorio"
        );

        btnAtendido.setToolTipText(
                "Marcar la cita seleccionada como atendida"
        );

        btnCancelar.setToolTipText(
                "Cancelar la cita seleccionada"
        );

        /*
         * Eventos originales.
         */
        btnConsultorio.addActionListener(
                e -> cambiarEstadoCitaSeleccionada(
                        Cita.Estado.EN_CONSULTORIO
                )
        );

        btnAtendido.addActionListener(
                e -> cambiarEstadoCitaSeleccionada(
                        Cita.Estado.ATENDIDO
                )
        );

        btnCancelar.addActionListener(
                e -> cambiarEstadoCitaSeleccionada(
                        Cita.Estado.CANCELADO
                )
        );

        botones.add(
                btnConsultorio
        );

        botones.add(
                btnAtendido
        );

        botones.add(
                btnCancelar
        );

        lblEstado =
                new JLabel(
                        "Selecciona una cita y cambia su estado."
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

        panelAcciones.add(
                botones,
                BorderLayout.WEST
        );

        panelAcciones.add(
                lblEstado,
                BorderLayout.CENTER
        );

        return panelAcciones;
    }



    private void iniciarAutoRefresh() {
        new Timer(
                10000,
                e -> cargarDatos()
        ).start();
    }

    public void setOnCitaFinalizada(
            Runnable callback
    ) {
        this.onCitaFinalizada =
                callback;
    }

    private void cambiarEstadoCitaSeleccionada(
            Cita.Estado estado
    ) {
        int filaVista =
                tablaCitas.getSelectedRow();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una cita primero.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int filaModelo =
                tablaCitas.convertRowIndexToModel(
                        filaVista
                );

        long id =
                ((Number) modeloTabla.getValueAt(
                        filaModelo,
                        0
                )).longValue();

        lblEstado.setText(
                "Actualizando estado de la cita..."
        );

        GestorCitas
                .getInstancia()
                .cambiarEstado(
                        id,
                        estado
                )
                .thenAccept(resultado ->
                        SwingUtilities.invokeLater(
                                this::cargarDatos
                        )
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "No se pudo actualizar la cita."
                        );

                        JOptionPane.showMessageDialog(
                                this,
                                "No se pudo actualizar: "
                                        + RespuestaHttp.mensaje(
                                        error
                                ),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    });

                    return null;
                });
    }

    @Override
    public void actualizar(
            String nuevoEstado,
            long citaId
    ) {
        SwingUtilities.invokeLater(() -> {
            cargarDatos();

            lblEstado.setText(
                    "Cita #"
                            + citaId
                            + " → "
                            + nuevoEstado
            );

            boolean esFinal =
                    "ATENDIDO".equals(
                            nuevoEstado
                    )
                            || "CANCELADO".equals(
                            nuevoEstado
                    );

            if (esFinal
                    && onCitaFinalizada != null) {
                onCitaFinalizada.run();
            }
        });
    }

    public void cargarDatos() {
        lblEstado.setText(
                "Cargando citas..."
        );

        GestorCitas
                .getInstancia()
                .getCitasVigentes()
                .thenAccept(citas ->
                        SwingUtilities.invokeLater(() -> {
                            modeloTabla.setRowCount(
                                    0
                            );

                            int cantidadProgramadas =
                                    0;

                            int cantidadConsultorio =
                                    0;

                            for (Cita cita : citas) {
                                modeloTabla.addRow(
                                        new Object[]{
                                                cita.getId(),

                                                cita.getPaciente() == null
                                                        ? "N/A"
                                                        : cita
                                                        .getPaciente()
                                                        .getNombre(),

                                                cita.getMedico(),

                                                cita.getFechaFormateada(),

                                                cita.getMotivo(),

                                                cita.getEstado() == null
                                                        ? "-"
                                                        : cita
                                                        .getEstado()
                                                        .name()
                                        }
                                );

                                /*
                                 * Conteos utilizados únicamente
                                 * para las tarjetas y el gráfico.
                                 */
                                if (cita.getEstado() != null) {
                                    String estadoActual =
                                            cita
                                                    .getEstado()
                                                    .name();

                                    if ("PROGRAMADA".equals(
                                            estadoActual
                                    )) {
                                        cantidadProgramadas++;
                                    }

                                    if ("EN_CONSULTORIO".equals(
                                            estadoActual
                                    )) {
                                        cantidadConsultorio++;
                                    }
                                }
                            }

                            cardTotalCitas.setValor(
                                    String.valueOf(
                                            citas.size()
                                    )
                            );

                            cardTotalCitas.setDescripcion(
                                    citas.size() == 1
                                            ? "Cita vigente registrada"
                                            : "Citas vigentes registradas"
                            );

                            cardProgramadas.setValor(
                                    String.valueOf(
                                            cantidadProgramadas
                                    )
                            );

                            cardProgramadas.setDescripcion(
                                    cantidadProgramadas == 1
                                            ? "Cita esperando atención"
                                            : "Citas esperando atención"
                            );

                            cardConsultorio.setValor(
                                    String.valueOf(
                                            cantidadConsultorio
                                    )
                            );

                            cardConsultorio.setDescripcion(
                                    cantidadConsultorio == 1
                                            ? "Cita actualmente en atención"
                                            : "Citas actualmente en atención"
                            );

                            graficoCitas.actualizarDatos(
                                    cantidadProgramadas,
                                    cantidadConsultorio
                            );

                            /*
                             * Actualización visual del estado.
                             */
                            actualizarTarjetaSeleccionada();

                            lblEstado.setText(
                                    citas.size()
                                            + " cita(s) vigente(s)"
                            );
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "Error al cargar las citas."
                        );

                        cardTotalCitas.setValor(
                                "—"
                        );

                        cardTotalCitas.setDescripcion(
                                "No se pudieron cargar los datos"
                        );

                        cardProgramadas.setValor(
                                "—"
                        );

                        cardProgramadas.setDescripcion(
                                "No se pudieron cargar los datos"
                        );

                        cardConsultorio.setValor(
                                "—"
                        );

                        cardConsultorio.setDescripcion(
                                "No se pudieron cargar los datos"
                        );

                        graficoCitas.actualizarDatos(
                                0,
                                0
                        );
                    });

                    System.err.println(
                            "Error cargando dashboard: "
                                    + RespuestaHttp.mensaje(
                                    error
                            )
                    );

                    error.printStackTrace();

                    return null;
                });
    }
}