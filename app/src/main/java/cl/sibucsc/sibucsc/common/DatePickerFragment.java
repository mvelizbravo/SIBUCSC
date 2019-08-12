package cl.sibucsc.sibucsc.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import cl.sibucsc.sibucsc.R;

/**
 * Fragmento para la seleccion de una fecha en un calendario.
 */
public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener onDateSet;
    private boolean isModal = false;

    public DatePickerFragment() {
    }

    // Utilizado para mostrar el fragmento dinamicamente.
    public static DatePickerFragment newInstance() {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.isModal = true;
        return fragment;
    }

    // Inicializa los contenidos del fragmento de dialogo.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isModal) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            View rootView = inflater.inflate(R.layout.fragment_tesistas, container, false);
            return rootView;
        }
    }

    // Setter listener
    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSet = onDate;
    }
}
