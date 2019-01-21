package cl.sibucsc.sibucsc;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ExtravioActivity extends AppCompatActivity {

    private static final String TAG = "ExtravioActividad";
    private int colorActividad = R.color.colorCrusta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extravio);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, colorActividad));
        TextView titulo = (TextView) findViewById(R.id.toolbar_title);
        titulo.setText(R.string.ext_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Boton envio de formulario
        // TODO: 21-01-19 Validaciones formulario
        ((Button) findViewById(R.id.btnEnviar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener campos
                String rut = ((TextInputEditText) findViewById(R.id.inputRut)).getText().toString();
                String carrera = ((TextInputEditText) findViewById(R.id.inputCarrera)).getText().toString();
                String email = ((TextInputEditText) findViewById(R.id.inputEmail)).getText().toString();
                String telefono = ((TextInputEditText) findViewById(R.id.inputTelefono)).getText().toString();
                String comentarios = ((TextInputEditText) findViewById(R.id.inputComentarios)).getText().toString();

                // Generar mensaje
                StringBuilder sb = new StringBuilder();
                sb.append("Rut: ").append(rut).append("\n");
                sb.append("Carrera: ").append(carrera).append("\n");
                sb.append("Email: ").append(email).append("\n");
                sb.append("Telefono: ").append(telefono).append("\n");
                sb.append("Comentarios: ").append(comentarios).append("\n");
                String mensaje = sb.toString();

                // Iniciar actividad
                Intent mail = new Intent(Intent.ACTION_SEND);
                String[] destino = {getResources().getString(R.string.ext_mail_to)};
                mail.putExtra(Intent.EXTRA_EMAIL, destino);
                mail.putExtra(Intent.EXTRA_SUBJECT, "Extravio de carnet: " + rut);
                mail.putExtra(Intent.EXTRA_TEXT, mensaje);
                mail.setType("message/rfc822");

                // Verificar si es posible enviar formulario
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mail, 0);
                boolean isIntentSafe = activities.size() > 0;

                // Iniciar actividad
                if (isIntentSafe) {
                    try {
                        // Usar gmail por defecto de estar instalado
                        Toast.makeText(getApplicationContext(), R.string.ext_toast, Toast.LENGTH_SHORT).show();
                        ApplicationInfo ai = getPackageManager().getApplicationInfo("com.google.android.gm", 0);
                        mail.setPackage("com.google.android.gm");
                        startActivity(mail);
                        Log.i(TAG, "Envio de formulario por Gmail.");
                    } catch (PackageManager.NameNotFoundException e) {
                        startActivity(Intent.createChooser(mail, String.valueOf(R.string.ext_send_by)));
                        Log.i(TAG, "Envio de formulario por otra aplicación.");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.ext_error, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "No fue posible enviar el formulario.");
                }
            }
        });
    }
}
