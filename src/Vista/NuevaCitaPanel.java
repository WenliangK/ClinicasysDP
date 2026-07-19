package Vista;

import AbstractFactory.ClinicaFactory;
import AbstractFactory.PrivadaFactory;
import AbstractFactory.PublicaFactory;
import Componentes.DashboardCard;
import Componentes.ModernButton;
import Componentes.ModernComboBox;
import Componentes.ModernTextField;
import Componentes.RoundedPanel;
import Componentes.SectionHeader;
import Componentes.StatusBadge;
import Controlador.GestorCitas;
import Controlador.GestorMedicos;
import Controlador.GestorPacientes;
import Modelo.Medico;
import Modelo.Paciente;
import Modelo.Sala;
import Utilidades.RespuestaHttp;
import Utilidades.Validador;
import ui.styles.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class NuevaCitaPanel extends JPanel {

    private ModernComboBox<Paciente> cbPaciente;
    private ModernComboBox<Medico> cbMedico;
    private ModernComboBox<String> cbTipoAtencion;

    private ModernTextField txtEspecialidad;
    private ModernTextField txtMotivo;

    private JSpinner spinnerFecha;
    private JSpinner spinnerHora;
    private JSpinner spinnerSala;

    private ModernButton btnGuardar;
    private JLabel lblEstado;

    public NuevaCitaPanel() {
        configurarPanel();
        inicializarComponentes();
        cargarPacientes();
        cargarMedicos();
    }



    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    private void inicializarComponentes() {
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(
                BorderFactory.createEmptyBorder(20, 24, 24, 24)
        );

        SectionHeader encabezado = crearEncabezado();
        DashboardCard tarjeta = crearTarjetaFormulario();

        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        encabezado.getPreferredSize().height
                )
        );

        tarjeta.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)
        );

        contenido.add(encabezado);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(tarjeta);

        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.setOpaque(false);
        scrollPrincipal.getViewport().setOpaque(false);
        scrollPrincipal.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPrincipal.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPrincipal, BorderLayout.CENTER);
    }


    private SectionHeader crearEncabezado() {
        SectionHeader encabezado = new SectionHeader(
                "Registrar nueva cita",
                "Programa una nueva atención médica para un paciente.",
                UIStyles.PRIMARY,
                "✚"
        );

        encabezado.setRightComponent(
                new StatusBadge("NUEVA CITA")
        );

        return encabezado;
    }



    private DashboardCard crearTarjetaFormulario() {
        DashboardCard tarjeta = new DashboardCard(
                "Datos de la cita",
                "Selecciona el paciente, el médico, la sala y el horario de atención.",
                UIStyles.PRIMARY,
                "▣"
        );

        JPanel contenidoTarjeta = new JPanel(
                new BorderLayout(0, 20)
        );
        contenidoTarjeta.setOpaque(false);

        contenidoTarjeta.add(
                crearResumenSuperior(),
                BorderLayout.NORTH
        );
        contenidoTarjeta.add(
                crearFormulario(),
                BorderLayout.CENTER
        );
        contenidoTarjeta.add(
                crearPanelInferior(),
                BorderLayout.SOUTH
        );

        tarjeta.setContenido(contenidoTarjeta);
        tarjeta.setPreferredSize(new Dimension(0, 800));
        tarjeta.setMinimumSize(new Dimension(700, 800));

        return tarjeta;
    }

    private JPanel crearResumenSuperior() {
        JPanel panel = new JPanel(
                new BorderLayout(16, 0)
        );
        panel.setOpaque(false);

        JPanel informacion = new JPanel();
        informacion.setOpaque(false);
        informacion.setLayout(
                new BoxLayout(informacion, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel(
                "Información de la atención"
        );
        titulo.setFont(UIStyles.BUTTON);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(
                "Completa los campos necesarios para registrar correctamente la cita."
        );
        descripcion.setFont(UIStyles.SMALL);
        descripcion.setForeground(UIStyles.TEXT_SECONDARY);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        informacion.add(titulo);
        informacion.add(Box.createVerticalStrut(4));
        informacion.add(descripcion);

        panel.add(informacion, BorderLayout.CENTER);
        panel.add(
                new StatusBadge("FORMULARIO"),
                BorderLayout.EAST
        );

        return panel;
    }



    private JPanel crearFormulario() {
        inicializarCampos();

        JPanel formulario = new JPanel(
                new GridLayout(1, 2, 20, 0)
        );
        formulario.setOpaque(false);

        formulario.add(crearTarjetaDatosAtencion());
        formulario.add(crearTarjetaProgramacion());

        return formulario;
    }

    private void inicializarCampos() {
        cbPaciente = new ModernComboBox<>();

        cbTipoAtencion = new ModernComboBox<>(
                new String[]{
                        "PRIVADO",
                        "PUBLICO (SIS)"
                }
        );

        cbMedico = new ModernComboBox<>();
        cbMedico.addActionListener(
                e -> actualizarDatosMedico()
        );

        txtEspecialidad = new ModernTextField("", 20);
        txtEspecialidad.setEditable(false);
        txtEspecialidad.setPlaceholder(
                "Especialidad del médico"
        );

        txtMotivo = new ModernTextField("", 20);
        txtMotivo.setPlaceholder(
                "Motivo de la consulta"
        );

        spinnerSala = new JSpinner(
                new SpinnerNumberModel(1, 1, 20, 1)
        );

        spinnerFecha = new JSpinner(
                new SpinnerDateModel()
        );
        spinnerFecha.setEditor(
                new JSpinner.DateEditor(
                        spinnerFecha,
                        "dd/MM/yyyy"
                )
        );

        spinnerHora = new JSpinner(
                new SpinnerDateModel()
        );
        spinnerHora.setEditor(
                new JSpinner.DateEditor(
                        spinnerHora,
                        "HH:mm"
                )
        );

        aplicarEstiloSpinner(spinnerSala);
        aplicarEstiloSpinner(spinnerFecha);
        aplicarEstiloSpinner(spinnerHora);
    }

    private RoundedPanel crearTarjetaDatosAtencion() {
        RoundedPanel tarjeta = crearTarjetaGrupo();

        tarjeta.add(
                crearTituloGrupo(
                        "Paciente y atención",
                        "Selecciona al paciente y al profesional responsable."
                ),
                BorderLayout.NORTH
        );

        JPanel campos = new JPanel(
                new GridBagLayout()
        );
        campos.setOpaque(false);

        agregarCampo(
                campos,
                0,
                "Paciente",
                "Paciente que recibirá la atención.",
                cbPaciente
        );

        agregarCampo(
                campos,
                1,
                "Tipo de atención",
                "Modalidad privada o cobertura pública.",
                cbTipoAtencion
        );

        agregarCampo(
                campos,
                2,
                "Médico asignado",
                "Profesional encargado de la consulta.",
                cbMedico
        );

        agregarCampo(
                campos,
                3,
                "Especialidad",
                "Se completa según el médico seleccionado.",
                txtEspecialidad
        );

        tarjeta.add(campos, BorderLayout.CENTER);

        tarjeta.setPreferredSize(
                new Dimension(420, 560)
        );
        tarjeta.setMinimumSize(
                new Dimension(340, 560)
        );

        return tarjeta;
    }

    private RoundedPanel crearTarjetaProgramacion() {
        RoundedPanel tarjeta = crearTarjetaGrupo();

        tarjeta.add(
                crearTituloGrupo(
                        "Programación de la cita",
                        "Define el consultorio, motivo, fecha y hora."
                ),
                BorderLayout.NORTH
        );

        JPanel campos = new JPanel(
                new GridBagLayout()
        );
        campos.setOpaque(false);

        agregarCampo(
                campos,
                0,
                "Sala",
                "Número de consultorio disponible.",
                spinnerSala
        );

        agregarCampo(
                campos,
                1,
                "Motivo de consulta",
                "Describe brevemente la razón de la atención.",
                txtMotivo
        );

        agregarCampo(
                campos,
                2,
                "Fecha",
                "Selecciona el día programado.",
                spinnerFecha
        );

        agregarCampo(
                campos,
                3,
                "Hora",
                "Selecciona la hora de atención.",
                spinnerHora
        );

        tarjeta.add(campos, BorderLayout.CENTER);

        tarjeta.setPreferredSize(
                new Dimension(420, 560)
        );
        tarjeta.setMinimumSize(
                new Dimension(340, 560)
        );

        return tarjeta;
    }

    private RoundedPanel crearTarjetaGrupo() {
        RoundedPanel tarjeta = new RoundedPanel(
                new BorderLayout(0, 18)
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyles.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        return tarjeta;
    }

    private JPanel crearTituloGrupo(
            String tituloTexto,
            String descripcionTexto
    ) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(UIStyles.BUTTON);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(descripcionTexto);
        descripcion.setFont(UIStyles.SMALL);
        descripcion.setForeground(UIStyles.TEXT_SECONDARY);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(descripcion);

        return panel;
    }

    private static void agregarCampo(
            JPanel panel,
            int fila,
            String tituloTexto,
            String ayudaTexto,
            JComponent campo
    ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(
                fila == 0 ? 0 : 18,
                0,
                0,
                0
        );

        JPanel bloque = new JPanel(
                new BorderLayout(0, 9)
        );
        bloque.setOpaque(false);

        JPanel encabezadoCampo = new JPanel();
        encabezadoCampo.setOpaque(false);
        encabezadoCampo.setLayout(
                new BoxLayout(
                        encabezadoCampo,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(UIStyles.NORMAL);
        titulo.setForeground(UIStyles.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ayuda = new JLabel(ayudaTexto);
        ayuda.setFont(UIStyles.SMALL);
        ayuda.setForeground(UIStyles.TEXT_SECONDARY);
        ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezadoCampo.add(titulo);
        encabezadoCampo.add(Box.createVerticalStrut(2));
        encabezadoCampo.add(ayuda);

        campo.setPreferredSize(
                new Dimension(220, 44)
        );
        campo.setMinimumSize(
                new Dimension(120, 44)
        );

        bloque.setPreferredSize(
                new Dimension(300, 86)
        );
        bloque.setMinimumSize(
                new Dimension(200, 86)
        );

        bloque.add(encabezadoCampo, BorderLayout.NORTH);
        bloque.add(campo, BorderLayout.CENTER);

        panel.add(bloque, gbc);
    }

    /*
     * =========================================================
     * PANEL INFERIOR
     * =========================================================
     */

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(
                new BorderLayout(18, 0)
        );
        panelInferior.setOpaque(false);
        panelInferior.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                UIStyles.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                0,
                                0,
                                0
                        )
                )
        );

        JPanel informacion = new JPanel();
        informacion.setOpaque(false);
        informacion.setLayout(
                new BoxLayout(
                        informacion,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel tituloEstado = new JLabel(
                "Estado del formulario"
        );
        tituloEstado.setFont(UIStyles.SMALL);
        tituloEstado.setForeground(UIStyles.TEXT);
        tituloEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEstado = new JLabel(
                "Completa todos los datos requeridos."
        );
        lblEstado.setFont(UIStyles.SMALL);
        lblEstado.setForeground(UIStyles.TEXT_SECONDARY);
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        informacion.add(tituloEstado);
        informacion.add(Box.createVerticalStrut(3));
        informacion.add(lblEstado);

        btnGuardar = new ModernButton(
                "Guardar cita",
                ModernButton.Tipo.PRIMARIO
        );
        btnGuardar.setPreferredSize(
                new Dimension(170, 42)
        );
        btnGuardar.addActionListener(
                e -> guardarCita()
        );

        JPanel panelBoton = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        0,
                        0
                )
        );
        panelBoton.setOpaque(false);
        panelBoton.add(btnGuardar);

        panelInferior.add(
                informacion,
                BorderLayout.CENTER
        );
        panelInferior.add(
                panelBoton,
                BorderLayout.EAST
        );

        return panelInferior;
    }

    /*
     * =========================================================
     * CARGA DE DATOS
     * =========================================================
     */

    public void cargarPacientes() {
        Paciente seleccionado =
                (Paciente) cbPaciente.getSelectedItem();

        Long idSeleccionado =
                seleccionado == null
                        ? null
                        : seleccionado.getId();

        lblEstado.setText(
                "Cargando pacientes..."
        );

        GestorPacientes
                .getInstancia()
                .getTodos()
                .thenAccept(lista ->
                        SwingUtilities.invokeLater(() -> {
                            cbPaciente.removeAllItems();

                            for (Paciente paciente : lista) {
                                cbPaciente.addItem(paciente);

                                if (idSeleccionado != null
                                        && idSeleccionado.equals(
                                        paciente.getId()
                                )) {
                                    cbPaciente.setSelectedItem(
                                            paciente
                                    );
                                }
                            }

                            lblEstado.setText(
                                    "Pacientes cargados correctamente."
                            );
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "No se pudieron cargar los pacientes."
                        );

                        Validador.mostrarError(
                                this,
                                "No se pudieron cargar los pacientes: "
                                        + RespuestaHttp.mensaje(error)
                        );
                    });

                    return null;
                });
    }

    public void cargarMedicos() {
        Medico seleccionado =
                (Medico) cbMedico.getSelectedItem();

        Long idSeleccionado =
                seleccionado == null
                        ? null
                        : seleccionado.getId();

        lblEstado.setText(
                "Cargando médicos..."
        );

        GestorMedicos
                .getInstancia()
                .getTodos()
                .thenAccept(lista ->
                        SwingUtilities.invokeLater(() -> {
                            cbMedico.removeAllItems();

                            for (Medico medico : lista) {
                                cbMedico.addItem(medico);

                                if (idSeleccionado != null
                                        && idSeleccionado.equals(
                                        medico.getId()
                                )) {
                                    cbMedico.setSelectedItem(
                                            medico
                                    );
                                }
                            }

                            actualizarDatosMedico();

                            lblEstado.setText(
                                    "Formulario preparado."
                            );
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        lblEstado.setText(
                                "No se pudieron cargar los médicos."
                        );

                        Validador.mostrarError(
                                this,
                                "No se pudieron cargar los médicos: "
                                        + RespuestaHttp.mensaje(error)
                        );
                    });

                    return null;
                });
    }

    private void actualizarDatosMedico() {
        Medico medico =
                (Medico) cbMedico.getSelectedItem();

        txtEspecialidad.setText(
                medico == null
                        ? ""
                        : medico.getEspecialidad()
        );

        if (medico != null
                && medico.getTipo() != null) {
            cbTipoAtencion.setSelectedItem(
                    "PUBLICO".equals(
                            medico.getTipo()
                    )
                            ? "PUBLICO (SIS)"
                            : "PRIVADO"
            );
        }
    }

    /*
     * =========================================================
     * REGISTRO DE LA CITA
     * =========================================================
     */

    private void guardarCita() {
        Paciente paciente =
                (Paciente) cbPaciente.getSelectedItem();

        Medico medicoSeleccionado =
                (Medico) cbMedico.getSelectedItem();

        if (paciente == null) {
            Validador.mostrarError(
                    this,
                    "Selecciona un paciente activo."
            );
            return;
        }

        if (medicoSeleccionado == null) {
            Validador.mostrarError(
                    this,
                    "Selecciona un médico activo."
            );
            return;
        }

        String motivo =
                txtMotivo.getText().trim();

        if (motivo.isEmpty()) {
            Validador.mostrarError(
                    this,
                    "Ingresa el motivo de la consulta."
            );
            return;
        }

        int numeroSala =
                (int) spinnerSala.getValue();

        Date fechaValor =
                (Date) spinnerFecha.getValue();

        Date horaValor =
                (Date) spinnerHora.getValue();

        LocalDate fecha =
                fechaValor
                        .toInstant()
                        .atZone(
                                ZoneId.systemDefault()
                        )
                        .toLocalDate();

        LocalTime hora =
                horaValor
                        .toInstant()
                        .atZone(
                                ZoneId.systemDefault()
                        )
                        .toLocalTime();

        LocalDateTime fechaHora =
                LocalDateTime.of(fecha, hora);

        if (fechaHora.isBefore(
                LocalDateTime.now()
        )) {
            Validador.mostrarError(
                    this,
                    "La fecha y hora de la cita no pueden estar en el pasado."
            );
            return;
        }

        String tipo =
                (String) cbTipoAtencion.getSelectedItem();

        ClinicaFactory factory =
                tipo.startsWith("PRIVADO")
                        ? new PrivadaFactory()
                        : new PublicaFactory();

        Medico medico =
                factory.crearMedico(
                        medicoSeleccionado.getId(),
                        medicoSeleccionado.getNombre(),
                        medicoSeleccionado.getEspecialidad()
                );

        Sala sala =
                factory.crearSala(
                        numeroSala,
                        "Consultorio de "
                                + medico.getEspecialidad()
                );

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");
        lblEstado.setText(
                "Registrando la cita..."
        );

        GestorCitas
                .getInstancia()
                .registrarCita(
                        paciente,
                        medico,
                        fechaHora,
                        motivo,
                        sala.getNumero()
                )
                .thenAccept(cita ->
                        SwingUtilities.invokeLater(() -> {
                            btnGuardar.setEnabled(true);
                            btnGuardar.setText(
                                    "Guardar cita"
                            );

                            lblEstado.setText(
                                    "Cita registrada correctamente."
                            );

                            Validador.mostrarExito(
                                    this,
                                    "Cita registrada correctamente."
                                            + "\nMédico: "
                                            + medico
                                            + "\nSala: "
                                            + sala
                            );

                            txtMotivo.setText("");
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(
                                "Guardar cita"
                        );

                        lblEstado.setText(
                                "No se pudo registrar la cita."
                        );

                        Validador.mostrarError(
                                this,
                                RespuestaHttp.mensaje(error)
                        );
                    });

                    return null;
                });
    }

    /*
     * =========================================================
     * ESTILO DE LOS SPINNERS
     * =========================================================
     */

    private static void aplicarEstiloSpinner(
            JSpinner spinner
    ) {
        spinner.setFont(UIStyles.NORMAL);
        spinner.setPreferredSize(
                new Dimension(220, 44)
        );
        spinner.setMinimumSize(
                new Dimension(120, 44)
        );

        spinner.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyles.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                4,
                                8,
                                4,
                                8
                        )
                )
        );

        JComponent editor = spinner.getEditor();

        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField campo =
                    ((JSpinner.DefaultEditor) editor)
                            .getTextField();

            campo.setFont(UIStyles.NORMAL);
            campo.setForeground(UIStyles.TEXT);
            campo.setBackground(Color.WHITE);
            campo.setBorder(null);
        }
    }
}