package cl.sibucsc.sibucsc.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cl.sibucsc.sibucsc.R;
import cl.sibucsc.sibucsc.model.Notebook;
import cl.sibucsc.sibucsc.recyclerview.NotebookFragment.NotebookListener;

/**
 * Este {@link RecyclerView.Adapter} provee vistas a un {@link RecyclerView} con
 * datos de tipo {@link Notebook} obtenidos desde 'mValues'. Tambien se encarga de
 * generar la llamada a el {@link NotebookListener} especificado.
 */
public class NotebookAdapter extends RecyclerView.Adapter<NotebookAdapter.ViewHolder> {

    private static final String TAG = "NotebookAdaptador";
    private int colorActividad = R.color.colorPine;

    private Context mContext; // Contexto desde el fragmento.
    private final List<Notebook> mValues; // ArrayList con notebooks.
    private final NotebookListener mListener; // Listener desde la actividad.

    public NotebookAdapter(Context context, List<Notebook> items, NotebookListener listener) {
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
    public int getItemDisponibles() {
        int count = 0;
        for (Notebook notebook : mValues) {
            if (notebook.getEstado().equals("Disponible")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Este {@link RecyclerView.ViewHolder} provee una referencia de la vista del
     * item {@link Notebook} y sus metadatos.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView; // vista
        public Notebook mItem; // item
        public boolean codigoBarra; // permite el cambio de titulo

        public ImageView imagen; // referencia
        public TextView titulo; // copia o codigo de barras
        public TextView subtitulo; // fecha devolucion
        public TextView descripcion; // estado

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imagen = itemView.findViewById(R.id.referencia);
            titulo = itemView.findViewById(R.id.titulo);
            subtitulo = itemView.findViewById(R.id.subtitulo);
            descripcion = itemView.findViewById(R.id.estado);
            codigoBarra = true;
        }

        // Permite asignar los contenidos del item a la vista.
        public void setItem(Context context, Notebook item) {
            // Item
            this.mItem = item;
            // Imagen
            imagen.setImageResource(R.drawable.notebook);
            // Titulo
            titulo.setText("Notebook: " + item.getCopia());
            titulo.setTextColor(ContextCompat.getColor(context, colorActividad));
            // Fecha de devolucion
            String fechaDevolucion = getFormattedDate(item);
            subtitulo.setText(fechaDevolucion);
            // Disponibilidad
            String estado = item.getEstado();
            descripcion.setText(estado);
            if (estado.equals("Disponible")) {
                descripcion.setTextColor(ContextCompat.getColor(context, R.color.colorPine));
            } else {
                descripcion.setTextColor(ContextCompat.getColor(context, R.color.colorRossoCorsa));
            }
            // Listener onClick a la Vista.
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (codigoBarra) {
                        titulo.setText("Código: " + mItem.getCodigoBarra());
                        codigoBarra = false;
                    } else {
                        titulo.setText("Notebook: " + mItem.getCopia());
                        codigoBarra = true;
                    }
                }
            });
        }

        // Retorna un String con la fecha en formato deseado.
        private String getFormattedDate(Notebook item) {
            SimpleDateFormat formatoJson = new SimpleDateFormat("yyyyMMddHHmm");
            try {
                Date fecha = formatoJson.parse(item.getFechaVencimiento() + item.getHoraVencimiento());
                SimpleDateFormat formatoChile = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                return "Devolución: " + formatoChile.format(fecha);
            } catch (ParseException e) {
                return "";
            }
        }
    }
}
