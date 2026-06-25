package Vista;
import AbstractFactory.ClinicaFactory;
import AbstractFactory.PrivadaFactory;
import AbstractFactory.PublicaFactory;
import Controlador.GestorCitas;
import Controlador.GestorPacientes;
import Modelo.Paciente;
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
    private JComboBox<String> cbTipoAtencion;
    private JTextField txtMedico, txtMotivo;
    private JSpinner spinnerFecha, spinnerHora;

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
        GestorPacientes.getInstancia().getTodos().forEach(cbPaciente::addItem);

        cbTipoAtencion = new JComboBox<>(new String[]{"PRIVADO", "PUBLICO (SIS)"});

        txtMedico = new JTextField(20);
        txtMotivo = new JTextField(20);

        spinnerFecha = new JSpinner(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));

        spinnerHora = new JSpinner(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));

        agregarFila(form, gbc, 0, "Paciente:", cbPaciente);
        agregarFila(form, gbc, 1, "Tipo de atencion:", cbTipoAtencion);
        agregarFila(form, gbc, 2, "Medico asignado:", txtMedico);
        agregarFila(form, gbc, 3, "Motivo:", txtMotivo);
        agregarFila(form, gbc, 4, "Fecha:", spinnerFecha);
        agregarFila(form, gbc, 5, "Hora:", spinnerHora);

        JButton btnGuardar = new JButton("Guardar Cita");
        btnGuardar.addActionListener(e -> guardarCita());
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        form.add(btnGuardar, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void agregarFila(JPanel p, GridBagConstraints g, int fila, String lbl, JComponent campo) {
        g.gridx = 0; g.gridy = fila; g.gridwidth = 1; p.add(new JLabel(lbl), g);
        g.gridx = 1; p.add(campo, g);
    }

    private void guardarCita() {
        try {
            Paciente paciente = (Paciente) cbPaciente.getSelectedItem();
            if (paciente == null) { Validador.mostrarError(this, "Selecciona un paciente."); return; }
            if (txtMedico.getText().trim().isEmpty()) { Validador.mostrarError(this, "Ingresa el nombre del medico."); return; }
            if (txtMotivo.getText().trim().isEmpty())  { Validador.mostrarError(this, "Ingresa el motivo de la consulta."); return; }

            String tipo = (String) cbTipoAtencion.getSelectedItem();
            ClinicaFactory factory = tipo.startsWith("PRIVADO") ? new PrivadaFactory() : new PublicaFactory();

            Date fechaVal = (Date) spinnerFecha.getValue();
            Date horaVal  = (Date) spinnerHora.getValue();
            LocalDate ld = fechaVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime lt = horaVal.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDateTime fechaHora = LocalDateTime.of(ld, lt);

            GestorCitas.getInstancia().registrarCita(paciente, txtMedico.getText().trim(), fechaHora, txtMotivo.getText().trim());

            Validador.mostrarExito(this, "Cita registrada exitosamente!\nTipo de atencion: " + factory.crearMedico(0, "", "").getTipo());
            txtMedico.setText(""); txtMotivo.setText("");

        } catch (ExcepcionesPersonalizadas.FechaInvalidaException ex) {
            Validador.mostrarError(this, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
