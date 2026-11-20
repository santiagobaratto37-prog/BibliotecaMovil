package com.santiago.bibliotecamovil.datos;

import org.json.JSONException;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CatalogoApiTest {
    @Test
    public void leeLibroDelCatalogo() throws JSONException {
        String json = "[{\"idLibro\":14,\"titulo\":\"Cien años de soledad\","
                + "\"autor\":\"Gabriel García Márquez\",\"stock\":8,\"idCategoria\":1}]";
        assertEquals(Collections.singletonList(
                "Cien años de soledad\nGabriel García Márquez\nDisponibles: 8"),
                CatalogoApi.leerLibros(json));
    }

    @Test
    public void aceptaCatalogoVacio() throws JSONException {
        assertTrue(CatalogoApi.leerLibros("[]").isEmpty());
    }

    @Test(expected = JSONException.class)
    public void rechazaRespuestaSinStock() throws JSONException {
        CatalogoApi.leerLibros("[{\"titulo\":\"Libro\",\"autor\":\"Autor\"}]");
    }
}
