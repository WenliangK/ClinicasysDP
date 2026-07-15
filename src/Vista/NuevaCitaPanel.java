package Vista;

import AbstractFactory.ClinicaFactory;
import AbstractFactory.PrivadaFactory;
import AbstractFactory.PublicaFactory;
import Controlador.GestorCitas;
import Controlador.GestorMedicos;
import Controlador.GestorPacientes;
import Modelo.Medico;
import Modelo.Paciente;
import Modelo.Sala;
import Utilidades.RespuestaHttp;
import Utilidades.Validador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class NuevaCitaPanel extends JPanel {
    private JComboBox<Paciente> cbPaciente;
    private JComboBox<Medico> cbMedico;
    private JComboBox<String> cbTipoAtencion;
    private JTextField txtEspecialidad;
    private JTextField txtMotivo;
    private JSpinner spinnerFecha;
    private JSpinner spinnerHora;
    private JSpinner spinnerSala;
    private JButton btnGuardar;

    public NuevaCitaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("Registrar Nueva Cita");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos de la cita"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbPaciente = new JComboBox<>();
        cbTipoAtencion = new JComboBox<>(new String[]{"PRIVADO", "PUBLICO (SIS)"});
        cbMedico = new JComboBox<>();
        cbMedico.addActionListener(e -> actualizarDatosMedico());
        txtEspecialidad = new JTextField(20);
        txtEspecialidad.setEditable(false);
        txtMotivo = new JTextField(20);
        spinnerSala = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        spinnerHora = new JSpinner(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));

        agregarFila(formulario, gbc, 0, "Paciente:", cbPaciente);
        agregarFila(formulario, gbc, 1, "Tipo de atención:", cbTipoAtencion);
        agregarFila(formulario, gbc, 2, "Médico asignado:", cbMedico);
        agregarFila(formulario, gbc, 3, "Especialidad:", txtEspecialidad);
        agregarFila(formulario, gbc, 4, "Sala:", spinnerSala);
        agregarFila(formulario, gbc, 5, "Motivo:", txtMotivo);
        agregarFila(formulario, gbc, 6, "Fecha:", spinnerFecha);
        agregarFila(formulario, gbc, 7, "Hora:", spinnerHora);

        btnGuardar = new JButton("Guardar Cita");
        btnGuardar.addActionListener(e -> guardarCita());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        formulario.add(btnGuardar, gbc);
        add(formulario, BorderLayout.CENTER);

        cargarPacientes();
        cargarMedicos();
    }

    public void cargarPacientes() {
        // CORREGIDO: getTodos() devuelve CompletableFuture<List<Paciente>>,
        // no una List directa. Hay que esperar con thenAccept() y solo tocar
        // el JComboBox (componente Swing) dentro de invokeLater().
        GestorPacientes.getInstancia().getTodos()
                .thenAccept(lista -> SwingUtilities.invokeLater(() -> {
                    Object seleccionado = cbPaciente.getSelectedItem();
                    cbPaciente.removeAllItems();
                    lista.forEach(cbPaciente::addItem);
                    if (seleccionado != null) cbPaciente.setSelectedItem(seleccionado);
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() ->
                            Validador.mostrarError(this, "No se pudo cargar la lista de pacientes: " + ex.getMessage()));
                    return null;
                });
    }

    public void cargarMedicos() {
        // Mismo problema y misma corrección que cargarPacientes().
        GestorMedicos.getInstancia().getTodos()
                .thenAccept(lista -> SwingUtilities.invokeLater(() -> {
                    Object seleccionado = cbMedico.getSelectedItem();
                    cbMedico.removeAllItems();
                    lista.forEach(cbMedico::addItem);
                    if (seleccionado != null) cbMedico.setSelectedItem(seleccionado);
                    actualizarEspecialidad();
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() ->
                            Validador.mostrarError(this, "No se pudo cargar la lista de médicos: " + ex.getMessage()));
                    return null;
                });
    }

    private void actualizarDatosMedico() {
        Medico medico = (Medico) cbMedico.getSelectedItem();
        txtEspecialidad.setText(medico == null ? "" : medico.getEspecialidad());
        if (medico != null && medico.getTipo() != null) {
            cbTipoAtencion.setSelectedItem("PUBLICO".equals(medico.getTipo()) ? "PUBLICO (SIS)" : "PRIVADO");
        }
    }

    private void guardarCita() {
        try {
            Paciente paciente = (Paciente) cbPaciente.getSelectedItem();
            if (paciente == null) { Validador.mostrarError(this, "Selecciona un paciente."); return; }
            Medico medicoSeleccionado = (Medico) cbMedico.getSelectedItem();
            if (medicoSeleccionado == null) { Validador.mostrarError(this, "No hay medicos registrados. Registra uno en la tabla 'medicos'."); return; }
            if (txtMotivo.getText().trim().isEmpty())  { Validador.mostrarError(this, "Ingresa el motivo de la consulta."); return; }

            String tipo = (String) cbTipoAtencion.getSelectedItem();
            ClinicaFactory factory = tipo.startsWith("PRIVADO") ? new PrivadaFactory() : new PublicaFactory();

            int numeroSala = (int) spinnerSala.getValue();
            Medico medico = factory.crearMedico(
                    medicoSeleccionado.getId(), medicoSeleccionado.getNombre(), medicoSeleccionado.getEspecialidad());
            Sala sala = factory.crearSala(numeroSala, "Consultorio de " + medico.getEspecialidad());

            Date fechaVal = (Date) spinnerFecha.getValue();
            Date horaVal  = (Date) spinnerHora.getValue();
            LocalDate ld = fechaVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime lt = horaVal.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            // AÑADIDO: esta es la única de las tres excepciones que tiene
            // sentido validar aquí mismo, de forma síncrona, porque no
            // depende de datos del servidor (a diferencia de "cita duplicada"
            // o "sala ocupada", que necesitarían la lista de citas ya cargada).
            if (!Validador.validarFechaFutura(ld)) {
                throw new ExcepcionesPersonalizadas.FechaInvalidaException();
            }

            LocalDateTime fechaHora = LocalDateTime.of(ld, lt);

            String motivoTexto = txtMotivo.getText().trim();

            // CORREGIDO: registrarCita() ahora devuelve CompletableFuture<Cita>
            // (antes el método estaba vacío y esto ni siquiera hacía una
            // petición al servidor). El diálogo de éxito se muestra recién
            // cuando el servidor confirma que la guardó, no antes.
            btnGuardar.setEnabled(false);
            GestorCitas.getInstancia()
                    .registrarCita(paciente, medico.getNombre(), fechaHora, motivoTexto, sala.getNumero())
                    .thenAccept(citaGuardada -> SwingUtilities.invokeLater(() -> {
                        btnGuardar.setEnabled(true);
                        Validador.mostrarExito(this,
                                "Cita registrada exitosamente!\n" +
                                        "Tipo de atencion: " + medico.getTipo() + "\n" +
                                        "Medico asignado: " + medico + "\n" +
                                        "Sala asignada: " + sala);
                        txtMotivo.setText("");
                    }))
                    .exceptionally(ex -> {
                        SwingUtilities.invokeLater(() -> {
                            btnGuardar.setEnabled(true);
                            Validador.mostrarError(this, "No se pudo registrar la cita: " + ex.getMessage());
                        });
                        return null;
                    });

        } catch (ExcepcionesPersonalizadas.FechaInvalidaException ex) {
            // CORREGIDO: CitaDuplicadaException y SalaOcupadaException salieron
            // del catch porque nada las lanzaba (por eso el error de compilación).
            // Si más adelante validas duplicados/sala ocupada contra la lista
            // de citas ya cargada, se vuelven a agregar aquí con su propio "throw".
            Validador.mostrarError(this, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        String motivo = txtMotivo.getText().trim();
        if (motivo.isEmpty()) {
            Validador.mostrarError(this, "Ingresa el motivo de la consulta.");
            return;
        }

        int numeroSala = (int) spinnerSala.getValue();
        Date fechaValor = (Date) spinnerFecha.getValue();
        Date horaValor = (Date) spinnerHora.getValue();
        LocalDate fecha = fechaValor.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime hora = horaValor.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
        if (fechaHora.isBefore(LocalDateTime.now())) {
            Validador.mostrarError(this, "La fecha y hora de la cita no pueden estar en el pasado.");
            return;
        }

        String tipo = (String) cbTipoAtencion.getSelectedItem();
        ClinicaFactory factory = tipo.startsWith("PRIVADO") ? new PrivadaFactory() : new PublicaFactory();
        Medico medico = factory.crearMedico(
                medicoSeleccionado.getId(), medicoSeleccionado.getNombre(), medicoSeleccionado.getEspecialidad());
        Sala sala = factory.crearSala(numeroSala, "Consultorio de " + medico.getEspecialidad());

        btnGuardar.setEnabled(false);
        GestorCitas.getInstancia().registrarCita(paciente, medico, fechaHora, motivo, sala.getNumero())
                .thenAccept(cita -> SwingUtilities.invokeLater(() -> {
                    btnGuardar.setEnabled(true);
                    Validador.mostrarExito(this,
                            "Cita registrada correctamente.\nMédico: " + medico + "\nSala: " + sala);
                    txtMotivo.setText("");
                }))
                .exceptionally(error -> {
                    SwingUtilities.invokeLater(() -> {
                        btnGuardar.setEnabled(true);
                        Validador.mostrarError(this, RespuestaHttp.mensaje(error));
                    });
                    return null;
                });
    }

    private static void agregarFila(JPanel panel, GridBagConstraints gbc, int fila,
                                    String etiqueta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }
}
