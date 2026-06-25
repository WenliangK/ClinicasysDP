package DAO;

import Modelo.Factura;

import java.sql.SQLException;
import java.util.List;

public interface FacturaDAO {
    void insertar(Factura factura) throws SQLException;
    Factura buscarPorId(int id) throws SQLException;
    List<Factura> listarTodas() throws SQLException;
}
