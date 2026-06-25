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

    @Override
    public void insertar(Factura factura) throws SQLException {
        String sql = "INSERT INTO facturacion (cita_id, monto_base, monto_adicional, total) VALUES (?, ?, ?, ?)";
        Connection con = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (factura.getCitaId() != null) {
                ps.setInt(1, factura.getCitaId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, factura.getDescripcion());
            ps.setDouble(3, factura.getCosto());
            ps.setTimestamp(4, Timestamp.valueOf(factura.getFechaEmision()));
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
        String sql = "SELECT id, cita_id, descripcion, costo, fecha_emision FROM facturas WHERE id = ?";
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
        String sql = "SELECT id, cita_id, descripcion, costo, fecha_emision FROM facturas ORDER BY id DESC";
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
        return new Factura(
                rs.getInt("id"),
                citaId,
                rs.getString("descripcion"),
                rs.getDouble("costo"),
                rs.getTimestamp("fecha_emision").toLocalDateTime()
        );
    }
}
