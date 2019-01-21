package cl.sibucsc.sibucsc.recyclerview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cl.sibucsc.sibucsc.R;
import cl.sibucsc.sibucsc.model.Tesista;
import cl.sibucsc.sibucsc.recyclerview.TesistaFragment.TesistaListener;

/**
 * Este {@link RecyclerView.Adapter} provee vistas a un {@link RecyclerView} con
 * datos de tipo {@link Tesista} obtenidos desde 'mValues'. Tambien se encarga de
 * generar la llamada a el {@link TesistaListener} especificado.
 */
public class TesistaAdapter extends RecyclerView.Adapter<TesistaAdapter.ViewHolder> {

    private static final String TAG = "TesistaAdaptador";
    private int colorActividad = R.color.colorVenice;

    private Context mContext; // Contexto desde el fragmento.
    private final List<Tesista> mValues; // ArrayList con notebooks.
    private final TesistaListener mListener; // Listener desde la actividad.

    public TesistaAdapter(Context context, List<Tesista> items, TesistaListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
    }

    // Crea nuevas vistas sin datos (Invocada por el layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.view_item_list, parent, false);
        return new ViewHolder(view);
    }

    // Remplaza los contenidos de la vista (Invocada por el layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "El elemento " + position + " fue incluido.");
        holder.setItem(mContext, mValues.get(position));
    }

    // Retorna el tamaño de la base de datos (Invocado por el layout manager)
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    // Retorna el numero de items con estado Disponible.
    public int getSalasDisponibles() {
        int count = 0;
        for (Tesista tesista : mValues) {
            if (tesista.getEstado().equals("Disponible")) {
                count++;
            }
        }
        return count;
    }

    // Retorna el numero de items con estado Reservada.
    public int getSalasReservadas() {
        int count = 0;
        for (Tesista tesista : mValues) {
            if (tesista.getEstado().equals("Reservada")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Este {@link RecyclerView.ViewHolder} provee una referencia de la vista del
     * item {@link Tesista} y sus metadatos.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView; // vista
        public Tesista mItem; // item
        public boolean mostrarFecha;

        public ImageView imagen; // referencia
        public TextView titulo; // sala
        public TextView subtitulo; // periodo
        public TextView descripcion; // estado

        private String auxFecha;
        private String auxBloque;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imagen = (ImageView) itemView.findViewById(R.id.referencia);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
            subtitulo = (TextView) itemView.findViewById(R.id.subtitulo);
            descripcion = (TextView) itemView.findViewById(R.id.estado);
            mostrarFecha = true;
        }

        // Permite asignar los contenidos del item a la vista.
        public void setItem(Context context, Tesista item) {
            // Item
            this.mItem = item;
            // Imagen
            imagen.setImageResource(R.drawable.main_ic_tesista);
            // Titulo
            titulo.setText(item.getSala());
            titulo.setTextColor(ContextCompat.getColor(context, colorActividad));
            // Periodo
            getFormattedDate(item);
            subtitulo.setText(auxBloque);
            // Disponibilidad
            String estado = item.getEstado();
            descripcion.setText(estado);
            if (estado.equals("Disponible")) {
                descripcion.setTextColor(ContextCompat.getColor(context, R.color.colorOliveDrab));
            } else if (estado.equals("Reservada")) {
                descripcion.setTextColor(ContextCompat.getColor(context, R.color.colorLochmara));
            } else {
                descripcion.setTextColor(ContextCompat.getColor(context, R.color.colorThunderbird));
            }
            // Listener onClick a la Vista.
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mostrarFecha) {
                        titulo.setText(auxFecha);
                        mostrarFecha = false;
                    } else {
                        titulo.setText(mItem.getSala());
                        mostrarFecha = true;
                    }
                }
            });
        }

        // Retorna un String con la fecha en formato deseado.
        // Obtiene la fecha y el bloque para su visualizacion
        private void getFormattedDate(Tesista item) {
            Log.d(TAG, "Item: " + item.toString());

            SimpleDateFormat fechaJson = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat horaJson = new SimpleDateFormat("HHmm");
            try {
                Date fInicio = fechaJson.parse(item.getFechaInicio());
                String aux = item.getHoraInicio();
                if (aux.length() < 4) {
                    aux = "0" + aux;
                }
                Date hInicio = horaJson.parse(aux);
                Date hTermino = horaJson.parse(item.getHoraTermino());

                SimpleDateFormat fechaChile = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat horaChile = new SimpleDateFormat("HH:mm");

                String fecha = fechaChile.format(fInicio);
                String horaInicio = horaChile.format(hInicio);
                String horaTermino = horaChile.format(hTermino);

                auxFecha = "Fecha: " + fecha;
                auxBloque = "Bloque: " + horaInicio + " - " + horaTermino;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
