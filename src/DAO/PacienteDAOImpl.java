package DAO;

import Modelo.Paciente;
import Singleton.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAOImpl implements PacienteDAO {

    @Override
    public void insertar(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO pacientes (nombre, dni, telefono, email) VALUES (?, ?, ?, ?)";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getDni());
            ps.setString(3, paciente.getTelefono());
            ps.setString(4, paciente.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    paciente.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE pacientes SET nombre = ?, dni = ?, telefono = ?, email = ? WHERE id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getDni());
            ps.setString(3, paciente.getTelefono());
            ps.setString(4, paciente.getEmail());
            ps.setInt(5, paciente.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE id = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, dni, telefono, email FROM pacientes WHERE id = ?";
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
    public Paciente buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT id, nombre, dni, telefono, email FROM pacientes WHERE dni = ?";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Paciente> listarTodos() throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, dni, telefono, email FROM pacientes ORDER BY id";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente(
                rs.getString("nombre"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("email")
        );
        p.setId(rs.getInt("id"));
        return p;
    }
}
