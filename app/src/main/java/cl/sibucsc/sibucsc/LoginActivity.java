package cl.sibucsc.sibucsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cl.sibucsc.sibucsc.common.LoadingDialog;
import cl.sibucsc.sibucsc.model.Alumno;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActividad";
    private static final String URL_BASE = "http://dev.sibucsc.cl/common/json_appusuario";
    public static final String EXTRA_MESSAGE = "ALUMNO";

    private TextInputLayout mLayout; // Layout para poner errores
    private EditText mRut; // Campo para escribir el rut
    private Button mLogin; // Boton para inicio de sesion
    private Alumno mAlumno; // Alumno que sera enviado

    private LoadingDialog dialog; // Mostrar dialogo de carga

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Borrar splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Detectar enter en el campo
        mRut = findViewById(R.id.editRutLogin);
        mRut.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d(TAG, "Inicio de sesion desde EditText.");
                    String rut = ((TextInputEditText) findViewById(R.id.editRutLogin)).getText().toString().trim();
                    attemptLogin(URL_BASE + "/" + rut + "/");
                    return true;
                }
                return false;
            }
        });

        // Boton inicio de sesion
        mLogin = findViewById(R.id.buttonLogin);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Inicio de sesion desde Button.");
                String rut = ((TextInputEditText) findViewById(R.id.editRutLogin)).getText().toString().trim();
                attemptLogin(URL_BASE + "/" + rut + "/");
            }
        });

        // Obtener preferencias desde MainActivity
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
        String json = preferences.getString("json", "");

        // Si existe una sesion, ir directamente...
        if (!json.equals("")) {
            Log.d(TAG, "Sesion cargada: " + json);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        dialog = new LoadingDialog(LoginActivity.this);
    }

    // Request datos alumno
    private void attemptLogin(String url) {
        Log.d(TAG, "Inicio de Query: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Respuesta: " + response);
                dialog.cerrar();

                // El usuario especificado no existe en Aleph
                if (response.equals("[{\"Error\": \"El usuario especificado no existe en Aleph\"}]")) {
                    Toast.makeText(LoginActivity.this, "El usuario especificado no existe en Aleph.", Toast.LENGTH_SHORT).show();
                }

                // Pertenece a la universidad
                else {
                    JSONArray jsonArray;
                    Gson gson = new Gson();
                    List<Alumno> alumnos = new ArrayList<>();

                    // Serializar alumno
                    try {
                        jsonArray = new JSONArray(response);
                        alumnos = Arrays.asList(gson.fromJson(jsonArray.toString(), Alumno[].class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Guardar informacion mAlumno
                    mAlumno = alumnos.get(0);
                    mAlumno.setSede(mAlumno.getSede().trim()); // Remover espacio en blanco
                    Log.d(TAG, "Alumno: " + mAlumno.toString());

                    // Ir al menu principal
                    startMainActivity();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                dialog.cerrar();
                Toast.makeText(LoginActivity.this, "No es posible conectarse con el servidor.", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
        dialog.mostrar();
    }

    // Realiza el cambio hacia el menu principal.
    private void startMainActivity() {
        Gson gson = new Gson();
        String extra = gson.toJson(mAlumno);
        Log.d(TAG, "Enviando: " + extra);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, extra);
        startActivity(intent);
        finish();
    }
}
