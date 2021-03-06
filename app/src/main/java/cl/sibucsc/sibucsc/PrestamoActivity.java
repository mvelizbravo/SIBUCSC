package cl.sibucsc.sibucsc;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import cl.sibucsc.sibucsc.model.Notebook;
import cl.sibucsc.sibucsc.model.Tesista;
import cl.sibucsc.sibucsc.recyclerview.NotebookFragment;
import cl.sibucsc.sibucsc.recyclerview.TesistaFragment;

public class PrestamoActivity extends AppCompatActivity
        implements NotebookFragment.NotebookListener,
        TesistaFragment.TesistaListener {

    private static final String TAG = "PrestamoActividad";

    private int mColor; // Color que tendra la toolbar del fragmento x
    private int mTitulo; // Titulo que tendra la toolbar del fragmento x
    private int mFragmento; // ID fragmento
    private String mSede;    // Sede del alumno
    private Toolbar toolbar; // Toolbar
    private TextView titulo; // Titulo toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        titulo = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Agregar fragmento
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mColor = bundle.getInt("color");
            mTitulo = bundle.getInt("titulo");
            mFragmento = bundle.getInt("fragmento");
            if (bundle.containsKey("sede")) {
                Log.d(TAG, "Se añadio la sede: " + mSede);
                mSede = bundle.getString("sede");
            }
            addFragment();
        }
    }

    // Agrega dinamicamente un fragmento a la actividad
    private void addFragment() {
        // Toolbar
        toolbar.setBackgroundColor(ContextCompat.getColor(this, mColor));
        titulo.setText(mTitulo);

        // Fragmento
        Fragment fragment;
        Log.i(TAG, "Fragmento: " + mFragmento);
        switch (mFragmento) {
            // Tesistas
            case 2:
                fragment = TesistaFragment.newInstance(1);
                getSupportFragmentManager().beginTransaction().add(R.id.prestamo, fragment).commit();
                break;
            // Notebooks
            case 3:
                fragment = NotebookFragment.newInstance(1);
                Bundle bundle = new Bundle();
                bundle.putString("sede", mSede);
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.prestamo, fragment).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onImageClicked(Notebook item) {
    }

    @Override
    public void onImageClicked(Tesista item) {
    }
}

