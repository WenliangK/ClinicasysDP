package DAO;

import Modelo.Factura;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface  FacturaDAO {
    CompletableFuture<List<Factura>> listarTodos();
    CompletableFuture<Factura> guardar(Factura factura);
    CompletableFuture<Void> eliminar(long id);
}
