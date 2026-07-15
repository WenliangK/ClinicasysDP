package Utilidades;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public final class RespuestaHttp {
    private RespuestaHttp() {
    }

    public static boolean esExitosa(HttpResponse<?> respuesta) {
        return respuesta.statusCode() >= 200 && respuesta.statusCode() < 300;
    }

    public static RuntimeException error(String operacion, HttpResponse<String> respuesta) {
        String detalle = extraerDetalle(respuesta.body());
        String mensaje = operacion + " (HTTP " + respuesta.statusCode() + ")";
        if (!detalle.isBlank()) {
            mensaje += ": " + detalle;
        }
        return new RuntimeException(mensaje);
    }

    public static String mensaje(Throwable error) {
        Throwable actual = error;
        while ((actual instanceof CompletionException || actual instanceof ExecutionException)
                && actual.getCause() != null) {
            actual = actual.getCause();
        }
        return actual.getMessage() == null ? actual.toString() : actual.getMessage();
    }

    private static String extraerDetalle(String cuerpo) {
        if (cuerpo == null || cuerpo.isBlank()) {
            return "";
        }
        try {
            JsonObject json = JsonParser.parseString(cuerpo).getAsJsonObject();
            if (json.has("error") && !json.get("error").isJsonNull()) {
                return json.get("error").getAsString();
            }
            if (json.has("detail") && !json.get("detail").isJsonNull()) {
                return json.get("detail").getAsString();
            }
        } catch (RuntimeException ignored) {
            // Si el servidor no devolvió JSON, se muestra el cuerpo sin procesar.
        }
        return cuerpo.length() > 300 ? cuerpo.substring(0, 300) + "..." : cuerpo;
    }
}
