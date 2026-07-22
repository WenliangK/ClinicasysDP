package Utilidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class ServicioCorreo {

    private static volatile ServicioCorreo instancia;

    private ServicioCorreo() {
    }

    public static ServicioCorreo getInstancia() {
        ServicioCorreo actual = instancia;

        if (actual == null) {
            synchronized (ServicioCorreo.class) {
                actual = instancia;

                if (actual == null) {
                    actual = new ServicioCorreo();
                    instancia = actual;
                }
            }
        }

        return actual;
    }


    public CompletableFuture<Void> enviarFactura(
            String destinatario,
            String nombrePaciente,
            Long facturaId,
            byte[] facturaPng
    ) {
        return CompletableFuture.runAsync(() -> {

            validarDatos(
                    destinatario,
                    facturaId,
                    facturaPng
            );

            try {
                // Simula el tiempo que tomaría enviar el correo.
                Thread.sleep(1200);

                String paciente = nombrePaciente == null
                        || nombrePaciente.isBlank()
                        ? "Paciente"
                        : nombrePaciente.trim();

                String fechaHora = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                );

                System.out.println();
                System.out.println("==========================================");
                System.out.println("       CORREO ENVIADO - SIMULACIÓN");
                System.out.println("==========================================");
                System.out.println("Destinatario : " + destinatario);
                System.out.println("Paciente     : " + paciente);
                System.out.println("Factura      : #" + facturaId);
                System.out.println("Archivo      : factura_" + facturaId + ".png");
                System.out.println("Tamaño       : " + facturaPng.length + " bytes");
                System.out.println("Fecha y hora : " + fechaHora);
                System.out.println("Estado       : ENVÍO SIMULADO CORRECTAMENTE");
                System.out.println("==========================================");
                System.out.println();

            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();

                throw new CompletionException(
                        new IllegalStateException(
                                "La simulación del envío fue interrumpida.",
                                error
                        )
                );
            }
        });
    }

    private static void validarDatos(
            String destinatario,
            Long facturaId,
            byte[] facturaPng
    ) {
        if (destinatario == null || destinatario.isBlank()) {
            throw new CompletionException(
                    new IllegalArgumentException(
                            "El paciente no tiene un correo registrado."
                    )
            );
        }

        if (!destinatario.contains("@")) {
            throw new CompletionException(
                    new IllegalArgumentException(
                            "El correo registrado no tiene un formato válido."
                    )
            );
        }

        if (facturaId == null) {
            throw new CompletionException(
                    new IllegalArgumentException(
                            "La factura todavía no tiene identificador."
                    )
            );
        }

        if (facturaPng == null || facturaPng.length == 0) {
            throw new CompletionException(
                    new IllegalArgumentException(
                            "No se pudo generar el archivo de la factura."
                    )
            );
        }
    }
}