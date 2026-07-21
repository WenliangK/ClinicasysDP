package Vista;

import Componentes.DashboardCard;
import Componentes.ModernButton;
import Componentes.ModernComboBox;
import Componentes.ModernScrollPane;
import Componentes.ModernTable;
import Componentes.ModernTextField;
import Componentes.RoundedPanel;
import Componentes.SectionHeader;
import Componentes.StatusBadge;
import Controlador.GestorMedicos;
import Modelo.Medico;
import Utilidades.RespuestaHttp;
import Utilidades.Validador;
import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MedicosPanel extends JPanel {

    private final GestorMedicos gestor =
            GestorMedicos.getInstancia();

    private final List<Medico> medicos =
            new ArrayList<>();

    private ModernTable tablaMedicos;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;

    private ModernButton btnNuevo;
    private ModernButton btnEditar;
    private ModernButton btnCambiarEstado;
    private ModernButton btnRefrescar;

    public MedicosPanel() {
        configurarPanel();
        inicializarComponentes();
        iniciarAutoRefresh();
        cargarTabla();
    }



    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.BACKGROUND);

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

        DashboardCard tarjeta =
                crearTarjetaPrincipal();

        encabezado.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        encabezado.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        encabezado.getPreferredSize().height
                )
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );

        contenido.add(encabezado);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(tarjeta);

        JScrollPane scrollPrincipal =
                new JScrollPane(contenido);

        scrollPrincipal.setBorder(null);
        scrollPrincipal.setOpaque(false);
        scrollPrincipal.getViewport().setOpaque(false);

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
                        "Directorio de médicos",
                        "Registra, consulta y administra la información del personal médico.",
                        UIStyles.PRIMARY,
                        "⚕"
                );

        StatusBadge badge =
                new StatusBadge("MÉDICOS");

        encabezado.setRightComponent(badge);

        return encabezado;
    }


    private DashboardCard crearTarjetaPrincipal() {
        DashboardCard tarjeta =
                new DashboardCard(
                        "Listado de médicos",
                        "Administra las especialidades, tipos de atención y estado del personal médico.",
                        UIStyles.PRIMARY,
                        "✚"
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
                crearPanelInformativo(),
                BorderLayout.NORTH
        );

        contenidoTarjeta.add(
                crearTabla(),
                BorderLayout.CENTER
        );

        contenidoTarjeta.add(
                crearPanelAcciones(),
                BorderLayout.SOUTH
        );

        tarjeta.setContenido(
                contenidoTarjeta
        );

        tarjeta.setPreferredSize(
                new Dimension(
                        0,
                        610
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

        panel.setOpaque(false);

        JLabel ayuda =
                new JLabel(
                        "Selecciona un médico para editarlo o cambiar su estado."
                );

        ayuda.setFont(UIStyles.SMALL);
        ayuda.setForeground(UIStyles.TEXT_SECONDARY);

        JLabel ayudaDobleClic =
                new JLabel(
                        "Doble clic para editar"
                );

        ayudaDobleClic.setFont(UIStyles.SMALL);
        ayudaDobleClic.setForeground(UIStyles.TEXT_SECONDARY);

        ayudaDobleClic.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        panel.add(
                ayuda,
                BorderLayout.WEST
        );

        panel.add(
                ayudaDobleClic,
                BorderLayout.EAST
        );

        return panel;
    }



    private ModernScrollPane crearTabla() {
        String[] columnas = {
                "ID",
                "Nombre del médico",
                "Especialidad",
                "Tipo",
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

        tablaMedicos =
                new ModernTable(
                        modeloTabla
                );

        tablaMedicos.setRowHeight(48);

        tablaMedicos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaMedicos.setAutoCreateRowSorter(true);

        tablaMedicos.setShowVerticalLines(false);
        tablaMedicos.setShowHorizontalLines(true);

        tablaMedicos.setGridColor(
                UIStyles.BORDER
        );

        tablaMedicos.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        tablaMedicos
                .getTableHeader()
                .setReorderingAllowed(false);

        tablaMedicos
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                44
                        )
                );

        tablaMedicos
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) {
                                actualizarBotonEstado();
                                actualizarBotonEditar();
                            }
                        }
                );

        tablaMedicos.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            MouseEvent evento
                    ) {
                        if (evento.getClickCount() == 2
                                && tablaMedicos.getSelectedRow() >= 0) {
                            editarSeleccionado();
                        }
                    }
                }
        );

        configurarColumnas();
        configurarRenderizadores();

        ModernScrollPane scrollPane =
                new ModernScrollPane(
                        tablaMedicos
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
                        420
                )
        );

        return scrollPane;
    }

    private void configurarColumnas() {
        tablaMedicos
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        tablaMedicos
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(80);

        tablaMedicos
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(260);

        tablaMedicos
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(220);

        tablaMedicos
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(140);

        tablaMedicos
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(140);
    }



    private void configurarRenderizadores() {
        DefaultTableCellRenderer centrado =
                crearRenderizadorCentrado();

        tablaMedicos
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(centrado);

        tablaMedicos
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        crearRenderizadorTipo()
                );

        tablaMedicos
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        crearRenderizadorEstado()
                );
    }

    private DefaultTableCellRenderer crearRenderizadorCentrado() {
        return new DefaultTableCellRenderer() {

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

                if (!seleccionado) {
                    componente.setBackground(
                            fila % 2 == 0
                                    ? UIStyles.CARD
                                    : UIStyles.BACKGROUND
                    );

                    componente.setForeground(
                            UIStyles.TEXT
                    );
                }

                return componente;
            }
        };
    }

    private TableCellRenderer crearRenderizadorTipo() {
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
                String tipo =
                        valor == null
                                ? ""
                                : valor.toString();

                StatusBadge badge =
                        new StatusBadge(tipo);

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
                                    ? UIStyles.CARD
                                    : UIStyles.BACKGROUND
                    );
                }

                contenedor.add(badge);

                return contenedor;
            }
        };
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
                        new StatusBadge(estado);

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
                                    ? UIStyles.CARD
                                    : UIStyles.BACKGROUND
                    );
                }

                contenedor.add(badge);

                return contenedor;
            }
        };
    }


    private JPanel crearPanelAcciones() {
        JPanel panelAcciones =
                new JPanel(
                        new BorderLayout(
                                16,
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

        btnNuevo =
                new ModernButton(
                        "Nuevo médico",
                        ModernButton.Tipo.PRIMARIO
                );

        btnEditar =
                new ModernButton(
                        "Editar",
                        ModernButton.Tipo.SECUNDARIO
                );

        btnCambiarEstado =
                new ModernButton(
                        "Desactivar",
                        ModernButton.Tipo.PELIGRO
                );

        btnRefrescar =
                new ModernButton(
                        "Refrescar",
                        ModernButton.Tipo.SECUNDARIO
                );

        btnNuevo.setToolTipText(
                "Registrar un nuevo médico"
        );

        btnEditar.setToolTipText(
                "Editar el médico seleccionado"
        );

        btnCambiarEstado.setToolTipText(
                "Activar o desactivar al médico seleccionado"
        );

        btnRefrescar.setToolTipText(
                "Actualizar el listado de médicos"
        );

        btnEditar.setEnabled(false);
        btnCambiarEstado.setEnabled(false);


        btnNuevo.addActionListener(
                e -> mostrarFormulario(null)
        );

        btnEditar.addActionListener(
                e -> editarSeleccionado()
        );

        btnCambiarEstado.addActionListener(
                e -> cambiarEstadoSeleccionado()
        );

        btnRefrescar.addActionListener(
                e -> cargarTabla()
        );

        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnCambiarEstado);
        botones.add(btnRefrescar);

        lblEstado =
                new JLabel(
                        "Preparando información..."
                );

        lblEstado.setFont(UIStyles.SMALL);
        lblEstado.setForeground(UIStyles.TEXT_SECONDARY);

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
                e -> cargarTabla()
        ).start();
    }



    public void cargarTabla() {
        lblEstado.setText(
                "Cargando médicos..."
        );

        btnRefrescar.setEnabled(false);
        btnRefrescar.setText("Cargando...");

        gestor.listarMedicos()
                .thenAccept(lista ->
                        SwingUtilities.invokeLater(() -> {
                            medicos.clear();
                            medicos.addAll(lista);

                            modeloTabla.setRowCount(0);

                            for (Medico medico : medicos) {
                                modeloTabla.addRow(
                                        new Object[]{
                                                medico.getId(),
                                                medico.getNombre(),
                                                medico.getEspecialidad(),
                                                medico.getTipo(),

                                                medico.isActivo()
                                                        ? "ACTIVO"
                                                        : "INACTIVO"
                                        }
                                );
                            }

                            lblEstado.setText(
                                    medicos.size()
                                            + " médico(s) registrado(s)"
                            );

                            btnRefrescar.setEnabled(true);
                            btnRefrescar.setText("Refrescar");

                            actualizarBotonEstado();
                            actualizarBotonEditar();
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "Error: "
                                        + RespuestaHttp.mensaje(error)
                        );

                        btnRefrescar.setEnabled(true);
                        btnRefrescar.setText("Refrescar");
                    });

                    return null;
                });
    }



    private void editarSeleccionado() {
        Medico medico =
                obtenerSeleccionado();

        if (medico == null) {
            mostrarAvisoSeleccion();
            return;
        }

        mostrarFormulario(medico);
    }



    private void mostrarFormulario(
            Medico actual
    ) {
        ModernTextField txtNombre =
                new ModernTextField(
                        actual == null
                                ? ""
                                : actual.getNombre(),
                        24
                );

        ModernTextField txtEspecialidad =
                new ModernTextField(
                        actual == null
                                ? ""
                                : actual.getEspecialidad(),
                        20
                );

        ModernComboBox<String> cbTipo =
                new ModernComboBox<>(
                        new String[]{
                                "PRIVADO",
                                "PUBLICO"
                        }
                );

        if (actual != null
                && actual.getTipo() != null) {
            cbTipo.setSelectedItem(
                    actual.getTipo()
            );
        }

        txtNombre.setPlaceholder(
                "Nombre completo del médico"
        );

        txtEspecialidad.setPlaceholder(
                "Ejemplo: Cardiología"
        );

        JPanel contenidoFormulario =
                new JPanel(
                        new BorderLayout(
                                0,
                                16
                        )
                );

        contenidoFormulario.setOpaque(false);

        contenidoFormulario.setPreferredSize(
                new Dimension(
                        520,
                        270
                )
        );

        contenidoFormulario.add(
                crearEncabezadoFormulario(actual),
                BorderLayout.NORTH
        );

        RoundedPanel formulario =
                new RoundedPanel(
                        new GridBagLayout()
                );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        agregarCampo(
                formulario,
                0,
                "Nombre completo",
                "Escribe los nombres y apellidos del médico.",
                txtNombre
        );

        agregarCampo(
                formulario,
                1,
                "Especialidad",
                "Indica el área médica del profesional.",
                txtEspecialidad
        );

        agregarCampo(
                formulario,
                2,
                "Tipo de atención",
                "Selecciona el tipo de servicio que brinda.",
                cbTipo
        );

        contenidoFormulario.add(
                formulario,
                BorderLayout.CENTER
        );

        while (true) {
            String titulo =
                    actual == null
                            ? "Registrar médico"
                            : "Editar médico";

            int opcion =
                    JOptionPane.showConfirmDialog(
                            this,
                            contenidoFormulario,
                            titulo,
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            if (txtNombre
                    .getText()
                    .trim()
                    .isEmpty()) {
                Validador.mostrarError(
                        this,
                        "Ingresa el nombre completo del médico."
                );

                continue;
            }

            if (txtEspecialidad
                    .getText()
                    .trim()
                    .isEmpty()) {
                Validador.mostrarError(
                        this,
                        "Ingresa la especialidad."
                );

                continue;
            }

            Medico medico =
                    new Medico(
                            actual == null
                                    ? null
                                    : actual.getId(),

                            txtNombre
                                    .getText()
                                    .trim(),

                            txtEspecialidad
                                    .getText()
                                    .trim(),

                            (String) cbTipo
                                    .getSelectedItem(),

                            actual == null
                                    || actual.isActivo()
                    );

            guardarMedico(medico);
            return;
        }
    }

    private JPanel crearEncabezadoFormulario(
            Medico actual
    ) {
        JPanel encabezado =
                new JPanel();

        encabezado.setOpaque(false);

        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        actual == null
                                ? "Información del nuevo médico"
                                : "Actualizar información del médico"
                );

        titulo.setFont(UIStyles.SUBTITLE);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion =
                new JLabel(
                        "Completa los datos profesionales antes de guardar."
                );

        descripcion.setFont(UIStyles.SMALL);
        descripcion.setForeground(UIStyles.TEXT_SECONDARY);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(descripcion);

        return encabezado;
    }



    private void guardarMedico(
            Medico medico
    ) {
        lblEstado.setText(
                "Guardando médico..."
        );

        gestor.guardar(medico)
                .thenAccept(guardado ->
                        SwingUtilities.invokeLater(() -> {
                            Validador.mostrarExito(
                                    this,
                                    "Médico guardado correctamente."
                            );

                            cargarTabla();
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "No se pudo guardar."
                        );

                        Validador.mostrarError(
                                this,
                                RespuestaHttp.mensaje(error)
                        );
                    });

                    return null;
                });
    }



    private void cambiarEstadoSeleccionado() {
        Medico medico =
                obtenerSeleccionado();

        if (medico == null) {
            mostrarAvisoSeleccion();
            return;
        }

        boolean nuevoEstado =
                !medico.isActivo();

        String accion =
                nuevoEstado
                        ? "reactivar"
                        : "desactivar";

        int opcion =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Deseas "
                                + accion
                                + " a "
                                + medico.getNombre()
                                + "?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        lblEstado.setText(
                "Actualizando estado del médico..."
        );

        gestor.cambiarActivo(
                        medico.getId(),
                        nuevoEstado
                )
                .thenAccept(guardado ->
                        SwingUtilities.invokeLater(
                                this::cargarTabla
                        )
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "No se pudo actualizar el estado."
                        );

                        Validador.mostrarError(
                                this,
                                RespuestaHttp.mensaje(error)
                        );
                    });

                    return null;
                });
    }



    private Medico obtenerSeleccionado() {
        int filaVista =
                tablaMedicos.getSelectedRow();

        if (filaVista < 0) {
            return null;
        }

        int filaModelo =
                tablaMedicos.convertRowIndexToModel(
                        filaVista
                );

        return filaModelo < medicos.size()
                ? medicos.get(filaModelo)
                : null;
    }

    private void actualizarBotonEstado() {
        Medico medico =
                obtenerSeleccionado();

        btnCambiarEstado.setEnabled(
                medico != null
        );

        boolean medicoInactivo =
                medico != null
                        && !medico.isActivo();

        if (medicoInactivo) {
            btnCambiarEstado.setText(
                    "Reactivar"
            );

            btnCambiarEstado.setTipo(
                    ModernButton.Tipo.EXITO
            );
        } else {
            btnCambiarEstado.setText(
                    "Desactivar"
            );

            btnCambiarEstado.setTipo(
                    ModernButton.Tipo.PELIGRO
            );
        }
    }

    private void actualizarBotonEditar() {
        btnEditar.setEnabled(
                obtenerSeleccionado() != null
        );
    }

    private void mostrarAvisoSeleccion() {
        JOptionPane.showMessageDialog(
                this,
                "Selecciona un médico primero.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }



    private static void agregarCampo(
            JPanel panel,
            int fila,
            String etiqueta,
            String ayuda,
            Component campo
    ) {
        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        7,
                        8,
                        7,
                        8
                );

        gbc.anchor =
                GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        JPanel panelEtiqueta =
                new JPanel();

        panelEtiqueta.setOpaque(false);

        panelEtiqueta.setLayout(
                new BoxLayout(
                        panelEtiqueta,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblCampo =
                new JLabel(etiqueta);

        lblCampo.setFont(UIStyles.NORMAL);
        lblCampo.setForeground(UIStyles.TEXT);
        lblCampo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAyuda =
                new JLabel(ayuda);

        lblAyuda.setFont(UIStyles.SMALL);
        lblAyuda.setForeground(UIStyles.TEXT_SECONDARY);
        lblAyuda.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelEtiqueta.add(lblCampo);
        panelEtiqueta.add(Box.createVerticalStrut(3));
        panelEtiqueta.add(lblAyuda);

        panel.add(
                panelEtiqueta,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(
                campo,
                gbc
        );
    }
}