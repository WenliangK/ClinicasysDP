package DAO;

import Modelo.Factura;
import Singleton.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAOImpl implements FacturaDAO {

    private static final String SELECT_BASE =
            "SELECT id, cita_id, paciente_id, paciente_nombre, paciente_dni, descripcion, costo, fecha_emision " +
                    "FROM facturas ";

    @Override
    public void insertar(Factura factura) throws SQLException {
        String sql = "INSERT INTO facturas (cita_id, paciente_id, paciente_nombre, paciente_dni, descripcion, costo, fecha_emision) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (factura.getCitaId() != null) {
                ps.setInt(1, factura.getCitaId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            if (factura.getPacienteId() != null) {
                ps.setInt(2, factura.getPacienteId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, factura.getPacienteNombre());
            ps.setString(4, factura.getPacienteDni());
            ps.setString(5, factura.getDescripcion());
            ps.setDouble(6, factura.getCosto());
            ps.setTimestamp(7, Timestamp.valueOf(factura.getFechaEmision()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    factura.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Factura buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + " WHERE id = ?";
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
    public List<Factura> listarTodas() throws SQLException {
        List<Factura> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY id DESC";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Factura mapear(ResultSet rs) throws SQLException {
        int citaIdRaw = rs.getInt("cita_id");
        Integer citaId = rs.wasNull() ? null : citaIdRaw;

        int pacienteIdRaw = rs.getInt("paciente_id");
        Integer pacienteId = rs.wasNull() ? null : pacienteIdRaw;

        return new Factura(
                rs.getInt("id"),
                citaId,
                pacienteId,
                rs.getString("paciente_nombre"),
                rs.getString("paciente_dni"),
                rs.getString("descripcion"),
                rs.getDouble("costo"),
                rs.getTimestamp("fecha_emision").toLocalDateTime()
        );
    }
}
