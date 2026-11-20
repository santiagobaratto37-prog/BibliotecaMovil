package com.santiago.bibliotecamovil;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.santiago.bibliotecamovil.datos.CatalogoApi;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final CatalogoApi catalogo = new CatalogoApi();
    private ArrayAdapter<String> libros;
    private Button consultar;
    private TextView estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (vista, insets) -> {
            androidx.core.graphics.Insets bordes =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(bordes.left, bordes.top, bordes.right, bordes.bottom);
            return insets;
        });

        consultar = findViewById(R.id.consultar);
        estado = findViewById(R.id.estado);
        ListView lista = findViewById(R.id.libros);
        libros = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lista.setAdapter(libros);
        consultar.setOnClickListener(vista -> cargarLibros());
    }

    private void cargarLibros() {
        consultar.setEnabled(false);
        estado.setText(R.string.cargando_libros);
        executor.execute(() -> {
            try {
                List<String> resultado = catalogo.consultar();
                runOnUiThread(() -> mostrarLibros(resultado));
            } catch (IOException | JSONException error) {
                Log.w("BibliotecaMovil", "Error al consultar el catálogo", error);
                runOnUiThread(() -> {
                    if (!isDestroyed()) {
                        estado.setText(R.string.error_catalogo);
                        consultar.setEnabled(true);
                    }
                });
            }
        });
    }

    private void mostrarLibros(List<String> resultado) {
        if (isDestroyed()) {
            return;
        }
        libros.clear();
        libros.addAll(resultado);
        estado.setText(resultado.isEmpty() ? getString(R.string.catalogo_vacio)
                : getResources().getQuantityString(R.plurals.libros_encontrados,
                        resultado.size(), resultado.size()));
        consultar.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
