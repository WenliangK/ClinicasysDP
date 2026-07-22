package Utilidades;

import jakarta.activation.DataHandler;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Envia la factura mediante SMTP sin almacenar credenciales en el codigo.
 *
 * Variables de entorno requeridas:
 * CLINICASYS_SMTP_USER
 * CLINICASYS_SMTP_PASSWORD
 *
 * Variables opcionales:
 * CLINICASYS_SMTP_HOST (por defecto smtp.gmail.com)
 * CLINICASYS_SMTP_PORT (por defecto 587)
 * CLINICASYS_SMTP_FROM (por defecto el usuario SMTP)
 */
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
            validarDatos(destinatario, facturaId, facturaPng);

            String host = variable("CLINICASYS_SMTP_HOST", "smtp.gmail.com");
            String puerto = variable("CLINICASYS_SMTP_PORT", "587");
            String usuario = variableRequerida("CLINICASYS_SMTP_USER");
            String password = variableRequerida("CLINICASYS_SMTP_PASSWORD");
            String remitente = variable("CLINICASYS_SMTP_FROM", usuario);

            Properties propiedades = new Properties();
            propiedades.put("mail.smtp.auth", "true");
            propiedades.put("mail.smtp.starttls.enable", "true");
            propiedades.put("mail.smtp.starttls.required", "true");
            propiedades.put("mail.smtp.host", host);
            propiedades.put("mail.smtp.port", puerto);
            propiedades.put("mail.smtp.connectiontimeout", "15000");
            propiedades.put("mail.smtp.timeout", "15000");
            propiedades.put("mail.smtp.writetimeout", "15000");

            Session sesion = Session.getInstance(
                    propiedades,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(usuario, password);
                        }
                    }
            );

            try {
                MimeMessage mensaje = new MimeMessage(sesion);
                mensaje.setFrom(new InternetAddress(remitente));
                mensaje.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(destinatario, false)
                );
                mensaje.setSubject(
                        "Factura de atención médica #" + facturaId,
                        "UTF-8"
                );

                MimeBodyPart cuerpo = new MimeBodyPart();
                cuerpo.setText(
                        "Estimado(a) "
                                + (nombrePaciente == null ? "paciente" : nombrePaciente)
                                + ",\n\nAdjuntamos la factura correspondiente a su atención médica."
                                + "\n\nGracias por confiar en nosotros.",
                        "UTF-8"
                );

                MimeBodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(
                        new DataHandler(
                                new ByteArrayDataSource(
                                        facturaPng,
                                        "image/png"
                                )
                        )
                );
                adjunto.setFileName("factura_" + facturaId + ".png");

                MimeMultipart contenido = new MimeMultipart();
                contenido.addBodyPart(cuerpo);
                contenido.addBodyPart(adjunto);
                mensaje.setContent(contenido);

                Transport.send(mensaje);
            } catch (MessagingException error) {
                throw new CompletionException(
                        new IllegalStateException(
                                "No se pudo enviar el correo: " + error.getMessage(),
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

    private static String variable(String nombre, String valorPorDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? valorPorDefecto : valor.trim();
    }

    private static String variableRequerida(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            throw new CompletionException(
                    new IllegalStateException(
                            "Falta configurar la variable de entorno " + nombre + "."
                    )
            );
        }
        return valor.trim();
    }
}
