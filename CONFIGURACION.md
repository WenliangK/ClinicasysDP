# Configuración del cliente

La URL de la API tiene una sola fuente de verdad. Antes de iniciar el cliente,
define la IP Tailscale del equipo que ejecuta Spring Boot:

```powershell
$env:CLINICASYS_API_URL = "http://100.x.y.z:8080/api"
..\servidor\mvnw.cmd -f pom.xml test
..\servidor\mvnw.cmd -f pom.xml exec:java
```

También se puede usar la propiedad de Java
`-Dclinicasys.api.url=http://100.x.y.z:8080/api`. Si no se configura ninguna,
el cliente usa `http://localhost:8080/api`.

Importa `pom.xml` como proyecto Maven en IntelliJ para que Gson y FlatLaf se
descarguen automáticamente, sin depender de archivos JAR en `Downloads`.
