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
import Utilidades.ExcepcionesPersonalizadas;
import Utilidades.Validador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class NuevaCitaPanel extends JPanel {

    private JComboBox<Paciente> cbPaciente;
    private JComboBox<Medico> cbMedico;
    private JComboBox<String> cbTipoAtencion;
    private JTextField txtEspecialidad, txtMotivo;
    private JSpinner spinnerFecha, spinnerHora;
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
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos de la cita"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbPaciente = new JComboBox<>();
        cargarPacientes();

        cbTipoAtencion = new JComboBox<>(new String[]{"PRIVADO", "PUBLICO (SIS)"});

        cbMedico = new JComboBox<>();
        cbMedico.addActionListener(e -> actualizarEspecialidad());

        txtEspecialidad = new JTextField(20);
        txtEspecialidad.setEditable(false);
        txtMotivo = new JTextField(20);

        cargarMedicos();

        spinnerSala = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

        spinnerFecha = new JSpinner(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));

        spinnerHora = new JSpinner(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));

        agregarFila(form, gbc, 0, "Paciente:", cbPaciente);
        agregarFila(form, gbc, 1, "Tipo de atencion:", cbTipoAtencion);
        agregarFila(form, gbc, 2, "Medico asignado:", cbMedico);
        agregarFila(form, gbc, 3, "Especialidad del medico:", txtEspecialidad);
        agregarFila(form, gbc, 4, "Sala:", spinnerSala);
        agregarFila(form, gbc, 5, "Motivo:", txtMotivo);
        agregarFila(form, gbc, 6, "Fecha:", spinnerFecha);
        agregarFila(form, gbc, 7, "Hora:", spinnerHora);

        btnGuardar = new JButton("Guardar Cita");
        btnGuardar.addActionListener(e -> guardarCita());
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        form.add(btnGuardar, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void agregarFila(JPanel p, GridBagConstraints g, int fila, String lbl, JComponent campo) {
        g.gridx = 0; g.gridy = fila; g.gridwidth = 1; p.add(new JLabel(lbl), g);
        g.gridx = 1; p.add(campo, g);
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

    private void actualizarEspecialidad() {
        Medico m = (Medico) cbMedico.getSelectedItem();
        txtEspecialidad.setText(m != null ? m.getEspecialidad() : "");
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
    }
}