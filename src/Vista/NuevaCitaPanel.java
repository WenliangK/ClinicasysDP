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
import Utilidades.RespuestaHttp;
import Utilidades.Validador;
import ui.styles.UIStyles;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NuevaCitaPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final String CARGANDO_HORARIOS =
            "Cargando horarios...";

    private static final String SIN_HORARIOS =
            "Sin horarios disponibles";

    private ModernComboBox<Paciente> cbPaciente;
    private ModernComboBox<Medico> cbMedico;
    private ModernComboBox<String> cbTipoAtencion;
    private ModernComboBox<String> cbHora;

    private ModernTextField txtEspecialidad;
    private ModernTextField txtMotivo;

    private JSpinner spinnerFecha;

    private ModernButton btnGuardar;
    private JLabel lblEstado;

    private long solicitudHorariosActual;

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
                "Selecciona el paciente, el médico y un horario disponible. La sala se asignará automáticamente.",
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
        cbPaciente.addActionListener(
                e -> cargarHorariosDisponibles()
        );

        cbTipoAtencion = new ModernComboBox<>(
                new String[]{
                        "PRIVADO",
                        "PUBLICO (SIS)"
                }
        );

        cbMedico = new ModernComboBox<>();
        cbMedico.addActionListener(e -> {
            actualizarDatosMedico();
            cargarHorariosDisponibles();
        });

        txtEspecialidad = new ModernTextField("", 20);
        txtEspecialidad.setEditable(false);
        txtEspecialidad.setPlaceholder(
                "Especialidad del médico"
        );

        txtMotivo = new ModernTextField("", 20);
        txtMotivo.setPlaceholder(
                "Motivo de la consulta"
        );

        ZoneId zona = ZoneId.systemDefault();
        Date inicioHoy = Date.from(
                LocalDate.now()
                        .atStartOfDay(zona)
                        .toInstant()
        );

        SpinnerDateModel modeloFecha = new SpinnerDateModel(
                inicioHoy,
                inicioHoy,
                null,
                Calendar.DAY_OF_MONTH
        );

        spinnerFecha = new JSpinner(modeloFecha);
        spinnerFecha.setEditor(
                new JSpinner.DateEditor(
                        spinnerFecha,
                        "dd/MM/yyyy"
                )
        );
        spinnerFecha.addChangeListener(
                e -> cargarHorariosDisponibles()
        );

        aplicarEstiloSpinner(spinnerFecha);

        cbHora = new ModernComboBox<>();
        cbHora.setEnabled(false);
        cbHora.addItem(SIN_HORARIOS);
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
                        "Los horarios ocupados se excluyen automáticamente."
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
                "Motivo de consulta",
                "Describe brevemente la razón de la atención.",
                txtMotivo
        );

        agregarCampo(
                campos,
                1,
                "Fecha",
                "Selecciona el día programado.",
                spinnerFecha
        );

        agregarCampo(
                campos,
                2,
                "Hora disponible",
                "Solo se muestran horarios libres para el paciente y el médico.",
                cbHora
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
        btnGuardar.setEnabled(false);
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

                            cargarHorariosDisponibles();
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
                            cargarHorariosDisponibles();

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

    private void cargarHorariosDisponibles() {
        if (cbHora == null
                || spinnerFecha == null
                || lblEstado == null
                || btnGuardar == null) {
            return;
        }

        Paciente paciente =
                (Paciente) cbPaciente.getSelectedItem();

        Medico medico =
                (Medico) cbMedico.getSelectedItem();

        LocalDate fecha = obtenerFechaSeleccionada();

        long solicitud = ++solicitudHorariosActual;

        cbHora.removeAllItems();
        cbHora.addItem(CARGANDO_HORARIOS);
        cbHora.setEnabled(false);
        btnGuardar.setEnabled(false);

        if (paciente == null || medico == null || fecha == null) {
            cbHora.removeAllItems();
            cbHora.addItem(SIN_HORARIOS);
            lblEstado.setText(
                    "Selecciona un paciente, un médico y una fecha."
            );
            return;
        }

        lblEstado.setText(
                "Consultando horarios disponibles..."
        );

        GestorCitas
                .getInstancia()
                .obtenerHorariosDisponibles(
                        paciente,
                        medico,
                        fecha
                )
                .thenAccept(horarios ->
                        SwingUtilities.invokeLater(() -> {
                            if (solicitud != solicitudHorariosActual) {
                                return;
                            }

                            mostrarHorarios(horarios);
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        if (solicitud != solicitudHorariosActual) {
                            return;
                        }

                        cbHora.removeAllItems();
                        cbHora.addItem(SIN_HORARIOS);
                        cbHora.setEnabled(false);
                        btnGuardar.setEnabled(false);

                        lblEstado.setText(
                                "No se pudieron consultar los horarios."
                        );

                        Validador.mostrarError(
                                this,
                                RespuestaHttp.mensaje(error)
                        );
                    });

                    return null;
                });
    }

    private void mostrarHorarios(List<LocalTime> horarios) {
        cbHora.removeAllItems();

        if (horarios == null || horarios.isEmpty()) {
            cbHora.addItem(SIN_HORARIOS);
            cbHora.setEnabled(false);
            btnGuardar.setEnabled(false);

            lblEstado.setText(
                    "No hay horarios disponibles para la fecha seleccionada."
            );
            return;
        }

        for (LocalTime horario : horarios) {
            cbHora.addItem(
                    horario.format(FORMATO_HORA)
            );
        }

        cbHora.setSelectedIndex(0);
        cbHora.setEnabled(true);
        btnGuardar.setEnabled(true);

        lblEstado.setText(
                horarios.size()
                        + " horario(s) disponible(s)."
        );
    }

    private LocalDate obtenerFechaSeleccionada() {
        if (spinnerFecha == null) {
            return null;
        }

        Date fechaValor =
                (Date) spinnerFecha.getValue();

        return fechaValor
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private LocalTime obtenerHoraSeleccionada() {
        if (cbHora == null || !cbHora.isEnabled()) {
            return null;
        }

        String valor =
                (String) cbHora.getSelectedItem();

        if (valor == null
                || valor.isBlank()
                || CARGANDO_HORARIOS.equals(valor)
                || SIN_HORARIOS.equals(valor)) {
            return null;
        }

        try {
            return LocalTime.parse(
                    valor,
                    FORMATO_HORA
            );
        } catch (RuntimeException error) {
            return null;
        }
    }

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

        LocalDate fecha = obtenerFechaSeleccionada();
        LocalTime hora = obtenerHoraSeleccionada();

        if (fecha == null || hora == null) {
            Validador.mostrarError(
                    this,
                    "Selecciona una fecha y un horario disponible."
            );
            return;
        }

        LocalDateTime fechaHora =
                LocalDateTime.of(fecha, hora);

        if (fechaHora.isBefore(
                LocalDateTime.now()
        )) {
            Validador.mostrarError(
                    this,
                    "La fecha y hora de la cita no pueden estar en el pasado."
            );
            cargarHorariosDisponibles();
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

        btnGuardar.setEnabled(false);
        cbHora.setEnabled(false);
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
                        motivo
                )
                .thenAccept(cita ->
                        SwingUtilities.invokeLater(() -> {
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
                                            + "\nFecha: "
                                            + fecha
                                            + "\nHora: "
                                            + hora.format(FORMATO_HORA)
                                            + "\nSala asignada: "
                                            + cita.getSalaId()
                            );

                            txtMotivo.setText("");
                            cargarHorariosDisponibles();
                        })
                )
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
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

                        cargarHorariosDisponibles();
                    });

                    return null;
                });
    }

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