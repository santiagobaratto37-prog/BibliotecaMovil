package com.santiago.bibliotecamovil.datos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class CatalogoApi {
    private static final String ENDPOINT =
            "http://127.0.0.1:8080/SistemaBibliotecaWeb/api/libros";

    public List<String> consultar() throws IOException, JSONException {
        HttpURLConnection conexion = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        try {
            conexion.setConnectTimeout(15000);
            conexion.setReadTimeout(5000);
            conexion.setInstanceFollowRedirects(false);
            conexion.setRequestProperty("Accept", "application/json");
            int estado = conexion.getResponseCode();
            if (estado != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + estado);
            }
            try (BufferedReader lector = new BufferedReader(new InputStreamReader(
                    conexion.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder json = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    json.append(linea).append('\n');
                }
                return leerLibros(json.toString());
            }
        } finally {
            conexion.disconnect();
        }
    }

    static List<String> leerLibros(String json) throws JSONException {
        JSONArray respuesta = new JSONArray(json);
        List<String> libros = new ArrayList<>();
        for (int i = 0; i < respuesta.length(); i++) {
            JSONObject libro = respuesta.getJSONObject(i);
            libros.add(libro.getString("titulo") + "\n" + libro.getString("autor")
                    + "\nDisponibles: " + libro.getInt("stock"));
        }
        return libros;
    }
}
