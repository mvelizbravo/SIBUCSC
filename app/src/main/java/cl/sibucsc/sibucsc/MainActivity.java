package cl.sibucsc.sibucsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cl.sibucsc.sibucsc.common.LogoutDialogFragment;
import cl.sibucsc.sibucsc.model.Alumno;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PrincipalActividad";
    private static final String URL_SIBUCSC = "http://sibucsc.cl";
    private static final String URL_CATALOGO = "http://catalogo.ucsc.cl";
    public static final String PREF_NAME = "Principal";

    private Button mPerfil; // Campo de texto para el rut
    private Button mLogout; // Boton para salir de sesion
    private Alumno mAlumno; // Datos alumno

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Borrar splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView titulo = (TextView) findViewById(R.id.toolbar_title);
        titulo.setAllCaps(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Datos sesion
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String jsonAlumno = preferences.getString("json", "");

        // Si no existe una sesion guardada, cargar desde login...
        Gson gson = new Gson();
        if (jsonAlumno.equals("")) {
            // Recibir extra...
            Intent intent = getIntent();
            String extra = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
            mAlumno = gson.fromJson(extra, Alumno.class);

            // Guardar preferencias...
            editor.putString("json", extra);
            editor.commit();
            Log.d(TAG, "Guardado: " + preferences.getString("json", ""));
        } else {
            // Cargar sesion
            Log.d(TAG, "Cargado: " + jsonAlumno);
            mAlumno = gson.fromJson(jsonAlumno, Alumno.class);
        }
        Log.d(TAG, "Alumno: " + mAlumno.toString());

        // Boton mostrar datos alumno, nombre en negrita
        mPerfil = (Button) findViewById(R.id.btnPerfil);
        SpannableStringBuilder builder = new SpannableStringBuilder("Bienvenido\n" + mAlumno.getNombre());
        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
        builder.setSpan(boldStyle, 10, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mPerfil.setText(builder);

        // Boton cerrar sesion
        mLogout = (Button) findViewById(R.id.btnLogout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutDialogFragment dialogFragment = new LogoutDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "logout");
            }
        });
    }

    /* 2. Salas Tesistas. */
    public void salasTesistas(View view) {
        Intent i = new Intent(this, PrestamoActivity.class);
        i.putExtra("color", R.color.colorVenice);
        i.putExtra("titulo", R.string.tes_title);
        i.putExtra("fragmento", 2);
        startActivity(i);
        Log.i(TAG, "Se inicio la actividad: TESISTAS");
    }

    /* 3. Prestamo de Notebooks. */
    public void notebookPrestamo(View view) {
        Intent i = new Intent(this, PrestamoActivity.class);
        i.putExtra("color", R.color.colorPine);
        i.putExtra("titulo", R.string.not_title);
        i.putExtra("fragmento", 3);
        i.putExtra("sede", mAlumno.getSede());
        startActivity(i);
        Log.i(TAG, "Se inicio la actividad: NOTEBOOKS");
    }

    /* 4. Revisar Sanciones Alumno. */
    public void revisarSanciones(View view) {
        Intent i = new Intent(this, SancionesActivity.class);
        i.putExtra("estado", mAlumno.getEstado());
        i.putExtra("sancion", mAlumno.getSancion());
        startActivity(i);
        Log.i(TAG, "Se inicio la actividad: SANCIONES");
    }

    /* 5. Extravio Carnet Biblioteca. */
    public void extravioCarnet(View view) {
        Intent i = new Intent(this, ExtravioActivity.class);
        startActivity(i);
        Log.i(TAG, "Se inicio la actividad: EXTRAVIO");
    }

    /* 6. Abrir Catalogo: Abre en el navegador la url definida en urlCatalogo. */
    public void abrirCatalogo(View view) {
        Toast.makeText(this, R.string.cat_toast, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_CATALOGO));
        startActivity(i);
        Log.i(TAG, "Se inicio la actividad: CATALOGO");
    }

    /* 7. Abrir SIBUCSC: Abre en el navegador la url definida en urlCatalogo. */
    public void abrirSIBUCSC(View view) {
        Toast.makeText(this, R.string.main_toast, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_SIBUCSC));
        startActivity(i);
        Log.i(TAG, "Se inicio la actividad: UCSC");
    }

    /**
     * Funcion para cerrar sesion llamado por {@link LogoutDialogFragment}
     */
    public void cerrarSesion() {
        // Borrar preferencias y volver al login.
        SharedPreferences pref = getSharedPreferences(PREF_NAME, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        Log.d(TAG, "Se ha cerrado sesion.");
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    // TODO: 24-01-19 AGREGAR SALUDO CUMPLEAÑOS / SANCION COMO DIALOGO Y FOTO GENERO
}
