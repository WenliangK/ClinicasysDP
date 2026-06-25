# Migracion de memoria a PostgreSQL — ClinicasysDP

## Que cambio

1. **Singleton/ConexionDB.java** — ahora abre una conexion JDBC real con
   `DriverManager.getConnection(...)`. Antes solo simulaba un booleano `conectado`.
2. **src/DAO/** (nuevo paquete) — interfaz + implementacion para cada entidad:
   - `PacienteDAO` / `PacienteDAOImpl`
   - `MedicoDAO` / `MedicoDAOImpl`
   - `CitaDAO` / `CitaDAOImpl` (hace JOIN con `pacientes` para reconstruir el objeto `Paciente` embebido en `Cita`)
   - `FacturaDAO` / `FacturaDAOImpl`
3. **Modelo/Paciente.java, Medico.java, Cita.java** — se agrego:
   - Un constructor *sin* id (para crear objetos nuevos antes de insertarlos).
   - `setId(int id)` para que el DAO asigne el id generado por PostgreSQL (`SERIAL`) tras el INSERT.
   - El constructor original con id se mantiene intacto, asi que nada que ya usaba `getId()`, `getNombre()`, etc. se rompe.
4. **Modelo/Factura.java** (nuevo) — representa la fila persistida de una boleta calculada por el Decorator (`Facturable`). El Decorator sigue calculando costo/descripcion en memoria; `Factura` es solo el registro que se guarda en la tabla `facturas`.
5. **Controlador/GestorPacientes, GestorCitas, GestorFacturacion** — ya no usan `ArrayList`; delegan cada operacion CRUD al DAO correspondiente. Las firmas publicas que ya usaba la Vista (`registrar`, `getTodos`, `registrarCita`, `getCitas`, `cambiarEstado`, `calcularFactura`, `generarBoleta`) se mantuvieron iguales para no tener que tocar los paneles de `Vista`.
6. **Vista/FacturacionPanel.java** — se agrego un boton "Guardar Factura en Base de Datos" que llama al nuevo `GestorFacturacion.guardarFactura(...)`, para que el `FacturaDAO` se use de verdad y no quede como codigo muerto.
7. **sql/schema.sql** (nuevo) — script para crear las 4 tablas en PostgreSQL.

## Por que tenias "cannot find symbol: getPacienteId()"

Tus DAOs probablemente llamaban a `paciente.getPacienteId()`, pero el modelo
`Paciente` solo define `getId()`. Mantuve `getId()` en los tres modelos (es lo
que ya usa toda tu capa `Vista`) y escribi los DAOs nuevos contra ese nombre,
para no tener mismatch.

## Pasos para correrlo en tu Fedora

1. Crea la base de datos y las tablas:
   ```bash
   sudo -u postgres psql
   CREATE DATABASE clinicasys_db;
   \c clinicasys_db
   \i /ruta/completa/a/sql/schema.sql
   \q
   ```
2. Abre `src/Singleton/ConexionDB.java` y ajusta `URL`, `USUARIO`, `PASSWORD`
   a tu instancia local (usuario/clave de tu rol de PostgreSQL).
3. En IntelliJ: clic derecho sobre `lib/postgresql-42.7.10.jar` → "Add as Library"
   (verifica que tambien este agregado `flatlaf-3.7.1.jar`).
4. Ejecuta `Main/App.java`.

## Notas

- `GestorPacientes`, `GestorCitas` y `GestorFacturacion` ahora lanzan
  `RuntimeException` si la consulta SQL falla (conexion caida, tabla
  inexistente, etc.), con el mensaje original de PostgreSQL incluido — revisa
  la consola si algo no guarda.
- `MedicoDAO` esta listo para usarse pero todavia no hay un panel de Vista
  para gestionar medicos (solo se usan strings libres en `NuevaCitaPanel`).
  Si luego quieres un `MedicosPanel` con persistencia real, el DAO ya esta
  preparado para conectarlo.
