package cl.sibucsc.sibucsc.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import cl.sibucsc.sibucsc.MainActivity;
import cl.sibucsc.sibucsc.R;

/**
 * Fragmento para la confirmación del cierre de sesion.
 */
public class LogoutDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.btn_logout)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.logout_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Llamar a la funcion para cerrar sesion.
                        ((MainActivity) getActivity()).cerrarSesion();
                    }
                })
                .setNegativeButton(R.string.logout_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cerrar el dialogo
                        LogoutDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
