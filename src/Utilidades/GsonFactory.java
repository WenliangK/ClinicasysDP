package Utilidades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GsonFactory {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final Gson INSTANCIA = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, tipo, ctx) ->
                    new JsonPrimitive(src.format(FORMATO_FECHA_HORA)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, tipo, ctx) ->
                    LocalDateTime.parse(json.getAsString(), FORMATO_FECHA_HORA))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, tipo, ctx) ->
                    new JsonPrimitive(src.format(FORMATO_FECHA)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, tipo, ctx) ->
                    LocalDate.parse(json.getAsString(), FORMATO_FECHA))
            .create();

    private GsonFactory() {}

    public static Gson getInstancia() {
        return INSTANCIA;
    }
}
