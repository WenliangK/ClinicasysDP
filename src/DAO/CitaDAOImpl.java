package DAO;

import DAO.CitaDAO;
import Modelo.Cita;
import Modelo.Paciente;
import Singleton.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CitaDAOImpl implements CitaDAO {

    private static final String SELECT_BASE =
            "SELECT c.id AS cita_id, c.medico, c.fecha_hora, c.motivo, c.estado, " +
                    "p.id AS paciente_id, p.nombre, p.dni, p.telefono, p.email " +
                    "FROM citas c " +
                    "JOIN pacientes p ON p.id = c.paciente_id ";

    @Override
    public void insertar(Cita cita) throws SQLException {
        String sql = "INSERT INTO citas (paciente_id, medico, fecha_hora, motivo, estado) VALUES (?, ?, ?, ?, ?)";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getPaciente().getId());
            ps.setString(2, cita.getMedico());
            ps.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            ps.setString(4, cita.getMotivo());
            ps.setString(5, cita.getEstado().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cita.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizarEstado(int citaId, Cita.Estado nuevoEstado) throws SQLException {
        String sql = "UPDATE citas SET estado = ? WHERE id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, citaId);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM citas WHERE id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Cita buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + " WHERE c.id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Cita> listarTodas() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY c.fecha_hora DESC";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Cita mapear(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente(
                rs.getString("nombre"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("email")
        );
        paciente.setId(rs.getInt("paciente_id"));

        Cita cita = new Cita(
                rs.getInt("cita_id"),
                paciente,
                rs.getString("medico"),
                rs.getTimestamp("fecha_hora").toLocalDateTime(),
                rs.getString("motivo"),
                Cita.Estado.valueOf(rs.getString("estado"))
        );
        return cita;
    }
}