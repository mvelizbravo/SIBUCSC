package cl.sibucsc.sibucsc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class SancionesActivity extends AppCompatActivity {

    private static final String TAG = "SancionesActividad";
    private int colorActividad = R.color.colorDisco;

    private TextView mSanciones; // Muestra el estado de las sanciones

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanciones);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, colorActividad));
        TextView titulo = (TextView) findViewById(R.id.toolbar_title);
        titulo.setText(R.string.san_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sanciones
        mSanciones = (TextView) findViewById(R.id.sanciones);
        Intent intent = getIntent();
        String estado = intent.getStringExtra("estado");
        String sancion = intent.getStringExtra("sancion");
        if (!estado.equals("")) {
            mSanciones.setText("Estado: " + estado + "\nFecha: " + sancion);
        } else {
            mSanciones.setText("Error al cargar las sanciones");
        }

    }
}
