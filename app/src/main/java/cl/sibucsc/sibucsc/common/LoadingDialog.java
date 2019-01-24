package cl.sibucsc.sibucsc.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import cl.sibucsc.sibucsc.R;

/**
 * Clase que maneja un {@link ProgressDialog}
 */
public class LoadingDialog {

    private ProgressDialog progressDialog;

    public LoadingDialog(Context context) {
        this.progressDialog = new ProgressDialog(context);
    }

    public void mostrar() {
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.view_loading_dialog);
    }

    public void cerrar() {
        progressDialog.dismiss();
    }
}
