package cl.sibucsc.sibucsc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SancionesActivity extends AppCompatActivity {

    private static final String TAG = "SancionesActividad";
    private int colorActividad = R.color.colorDisco;

    private TextView mEstado; // Muestra el estado de las sanciones
    private TextView mFecha; // Muestra la fecha de existir sancion

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
        mEstado = (TextView) findViewById(R.id.estadoSancion);
        mFecha = (TextView) findViewById(R.id.fechaSancion);

        Intent intent = getIntent();
        String estado = intent.getStringExtra("estado");
        String fecha = getFormattedDate(intent.getStringExtra("sancion"));
        Log.d(TAG, "Estado: " + estado);
        Log.d(TAG, "Fecha: " + fecha);

        if (estado.equals("Sancionado")) {
            mEstado.setText("Estás sancionado.");
            mFecha.setText(fecha);
        } else {
            mEstado.setText("No estás sancionado.");
            mFecha.setText("");
        }
    }

    // Retorna un String con la fecha en formato deseado.
    private String getFormattedDate(String yyyymmdd) {
        SimpleDateFormat formatoJson = new SimpleDateFormat("yyyyMMdd");
        try {
            Date fecha = formatoJson.parse(yyyymmdd);
            SimpleDateFormat formatoChile = new SimpleDateFormat("dd-MM-yyyy");
            return "Hasta: " + formatoChile.format(fecha) + ".";
        } catch (ParseException e) {
            return "";
        }
    }
}
