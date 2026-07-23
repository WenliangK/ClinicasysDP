package Vista;

import Componentes.DashboardCard;
import Componentes.ModernButton;
import Componentes.ModernScrollPane;
import Componentes.ModernTable;
import Componentes.ModernTextField;
import Componentes.RoundedPanel;
import Componentes.SectionHeader;
import Componentes.StatusBadge;
import Controlador.GestorPacientes;
import Modelo.Paciente;
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

public class PacientesPanel extends JPanel {

    private final GestorPacientes gestor =
            GestorPacientes.getInstancia();

    private final List<Paciente> pacientes =
            new ArrayList<>();

    private ModernTable tablaPacientes;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;

    private ModernButton btnNuevo;
    private ModernButton btnEditar;
    private ModernButton btnCambiarEstado;
    private ModernButton btnRefrescar;

    public PacientesPanel() {
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
                        "Directorio de pacientes",
                        "Registra, consulta y administra la información de los pacientes.",
                        UIStyles.PRIMARY,
                        "♙"
                );

        StatusBadge badge =
                new StatusBadge("PACIENTES");

        encabezado.setRightComponent(badge);

        return encabezado;
    }



    private DashboardCard crearTarjetaPrincipal() {
        DashboardCard tarjeta =
                new DashboardCard(
                        "Listado de pacientes",
                        "Administra los datos y el estado de cada paciente registrado.",
                        UIStyles.PRIMARY,
                        "☷"
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

        tarjeta.setContenido(contenidoTarjeta);

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
                        "Selecciona un paciente para editarlo o cambiar su estado."
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
                "Nombre completo",
                "DNI",
                "Teléfono",
                "Correo",
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

        tablaPacientes =
                new ModernTable(modeloTabla);

        tablaPacientes.setRowHeight(48);

        tablaPacientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaPacientes.setAutoCreateRowSorter(true);

        tablaPacientes.setShowVerticalLines(false);
        tablaPacientes.setShowHorizontalLines(true);

        tablaPacientes.setGridColor(
                UIStyles.BORDER
        );

        tablaPacientes.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        tablaPacientes
                .getTableHeader()
                .setReorderingAllowed(false);

        tablaPacientes
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                44
                        )
                );

        tablaPacientes
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) {
                                actualizarBotonEstado();
                                actualizarBotonEditar();
                            }
                        }
                );

        tablaPacientes.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            MouseEvent evento
                    ) {
                        if (evento.getClickCount() == 2
                                && tablaPacientes.getSelectedRow() >= 0) {
                            editarSeleccionado();
                        }
                    }
                }
        );

        configurarColumnas();
        configurarRenderizadores();

        ModernScrollPane scrollPane =
                new ModernScrollPane(tablaPacientes);

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
        tablaPacientes
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        tablaPacientes
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(80);

        tablaPacientes
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(220);

        tablaPacientes
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(110);

        tablaPacientes
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(125);

        tablaPacientes
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(230);

        tablaPacientes
                .getColumnModel()
                .getColumn(5)
                .setPreferredWidth(125);
    }

    private void configurarRenderizadores() {
        DefaultTableCellRenderer centrado =
                crearRenderizadorCentrado();

        tablaPacientes
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(centrado);

        tablaPacientes
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(centrado);

        tablaPacientes
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(centrado);

        tablaPacientes
                .getColumnModel()
                .getColumn(5)
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
                        "Nuevo paciente",
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
                "Registrar un nuevo paciente"
        );

        btnEditar.setToolTipText(
                "Editar el paciente seleccionado"
        );

        btnCambiarEstado.setToolTipText(
                "Activar o desactivar al paciente seleccionado"
        );

        btnRefrescar.setToolTipText(
                "Actualizar el listado de pacientes"
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
                "Cargando pacientes..."
        );

        btnRefrescar.setEnabled(false);
        btnRefrescar.setText("Cargando...");

        gestor.listarPacientes()
                .thenAccept(lista ->
                        SwingUtilities.invokeLater(() -> {
                            pacientes.clear();
                            pacientes.addAll(lista);

                            modeloTabla.setRowCount(0);

                            for (Paciente paciente : pacientes) {
                                modeloTabla.addRow(
                                        new Object[]{
                                                paciente.getId(),
                                                paciente.getNombre(),
                                                paciente.getDni(),
                                                paciente.getTelefono(),

                                                paciente.getEmail() == null
                                                        ? ""
                                                        : paciente.getEmail(),

                                                paciente.isActivo()
                                                        ? "ACTIVO"
                                                        : "INACTIVO"
                                        }
                                );
                            }

                            lblEstado.setText(
                                    pacientes.size()
                                            + " paciente(s) registrado(s)"
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
        Paciente paciente =
                obtenerSeleccionado();

        if (paciente == null) {
            mostrarAvisoSeleccion();
            return;
        }

        mostrarFormulario(paciente);
    }



    private void mostrarFormulario(
            Paciente actual
    ) {
        ModernTextField txtNombre =
                new ModernTextField(
                        actual == null
                                ? ""
                                : actual.getNombre(),
                        24
                );

        ModernTextField txtDni =
                new ModernTextField(
                        actual == null
                                ? ""
                                : actual.getDni(),
                        12
                );

        ModernTextField txtTelefono =
                new ModernTextField(
                        actual == null
                                ? ""
                                : actual.getTelefono(),
                        12
                );

        ModernTextField txtEmail =
                new ModernTextField(
                        actual == null
                                || actual.getEmail() == null
                                ? ""
                                : actual.getEmail(),
                        24
                );

        txtNombre.setPlaceholder(
                "Nombre completo del paciente"
        );

        txtDni.setPlaceholder(
                "8 dígitos"
        );

        txtTelefono.setPlaceholder(
                "9XXXXXXXX"
        );

        txtEmail.setPlaceholder(
                "correo@ejemplo.com"
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
                        320
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
                "Escribe nombres y apellidos del paciente.",
                txtNombre
        );

        agregarCampo(
                formulario,
                1,
                "DNI",
                "Debe contener exactamente 8 dígitos.",
                txtDni
        );

        agregarCampo(
                formulario,
                2,
                "Teléfono",
                "Debe contener 9 dígitos y comenzar con 9.",
                txtTelefono
        );

        agregarCampo(
                formulario,
                3,
                "Correo electrónico",
                "Este campo es opcional.",
                txtEmail
        );

        contenidoFormulario.add(
                formulario,
                BorderLayout.CENTER
        );

        while (true) {
            String titulo =
                    actual == null
                            ? "Registrar paciente"
                            : "Editar paciente";

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

            String error =
                    validarFormulario(
                            txtNombre,
                            txtDni,
                            txtTelefono,
                            txtEmail
                    );

            if (error != null) {
                Validador.mostrarError(
                        this,
                        error
                );

                continue;
            }

            Paciente paciente =
                    new Paciente(
                            actual == null
                                    ? null
                                    : actual.getId(),

                            txtNombre.getText().trim(),
                            txtDni.getText().trim(),
                            txtTelefono.getText().trim(),

                            txtEmail.getText()
                                    .trim()
                                    .isEmpty()
                                    ? null
                                    : txtEmail
                                    .getText()
                                    .trim(),

                            actual == null
                                    || actual.isActivo()
                    );

            guardarPaciente(paciente);
            return;
        }
    }

    private JPanel crearEncabezadoFormulario(
            Paciente actual
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
                                ? "Información del nuevo paciente"
                                : "Actualizar información del paciente"
                );

        titulo.setFont(UIStyles.SUBTITLE);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion =
                new JLabel(
                        "Completa los campos solicitados antes de guardar."
                );

        descripcion.setFont(UIStyles.SMALL);
        descripcion.setForeground(UIStyles.TEXT_SECONDARY);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(descripcion);

        return encabezado;
    }


    private String validarFormulario(
            JTextField nombre,
            JTextField dni,
            JTextField telefono,
            JTextField email
    ) {
        if (nombre.getText().trim().isEmpty()) {
            return "Ingresa el nombre completo.";
        }

        if (!Validador.validarDNI(
                dni.getText().trim()
        )) {
            return "El DNI debe contener exactamente 8 dígitos.";
        }

        if (!Validador.validarTelefono(
                telefono.getText().trim()
        )) {
            return "El teléfono debe contener 9 dígitos y comenzar con 9.";
        }

        if (!email.getText().trim().isEmpty()
                && !Validador.validarEmail(
                email.getText().trim()
        )) {
            return "El correo electrónico no tiene un formato válido.";
        }

        return null;
    }


    private void guardarPaciente(
            Paciente paciente
    ) {
        lblEstado.setText(
                "Guardando paciente..."
        );

        gestor.guardar(paciente)
                .thenAccept(guardado ->
                        SwingUtilities.invokeLater(() -> {
                            Validador.mostrarExito(
                                    this,
                                    "Paciente guardado correctamente."
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
        Paciente paciente =
                obtenerSeleccionado();

        if (paciente == null) {
            mostrarAvisoSeleccion();
            return;
        }

        boolean nuevoEstado =
                !paciente.isActivo();

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
                                + paciente.getNombre()
                                + "?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        lblEstado.setText(
                "Actualizando estado del paciente..."
        );

        gestor.cambiarActivo(
                        paciente.getId(),
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


    private Paciente obtenerSeleccionado() {
        int filaVista =
                tablaPacientes.getSelectedRow();

        if (filaVista < 0) {
            return null;
        }

        int filaModelo =
                tablaPacientes.convertRowIndexToModel(
                        filaVista
                );

        return filaModelo < pacientes.size()
                ? pacientes.get(filaModelo)
                : null;
    }

    private void actualizarBotonEstado() {
        Paciente paciente =
                obtenerSeleccionado();

        btnCambiarEstado.setEnabled(
                paciente != null
        );

        boolean pacienteInactivo =
                paciente != null
                        && !paciente.isActivo();

        if (pacienteInactivo) {
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
                "Selecciona un paciente primero.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }


    private static void agregarCampo(
            JPanel panel,
            int fila,
            String etiqueta,
            String ayuda,
            JTextField campo
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