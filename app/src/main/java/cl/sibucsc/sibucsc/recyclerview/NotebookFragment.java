package cl.sibucsc.sibucsc.recyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cl.sibucsc.sibucsc.R;
import cl.sibucsc.sibucsc.model.Notebook;
import cl.sibucsc.sibucsc.recyclerview.decoration.SimpleDividerItemDecoration;

/**
 * {@link Fragment} tiene la funcion de mostrar un listado de {@link Notebook} y
 * cuantificar cuantos de estos tienen estado 'Disponible'.
 * <p>
 * Las actividades que contengan este fragmento deben implementar la
 * interfaz {@link NotebookListener}.
 */
public class NotebookFragment extends Fragment {

    private static final String TAG = "NotebookPrestamo";
    private static final String URL_BASE = "http://dev.sibucsc.cl/usuarios/json_notebook/";
    private String sedeAlumno;

    // Numero de columnas y valor por defecto
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    // Plantilla
    private RecyclerView mRecyclerView; // Muestra varias vistas de un mismo tipo.
    private NotebookAdapter mAdapter; // Se encarga de asignar los datos y la vista al RV.
    private NotebookListener mListener; // Permite realizar acciones establecidas en la interfaz.
    private SwipeRefreshLayout mSwipeRefresh; // Se utiliza para actualizar los datos.
    private TextView mBanner; // Para visualizar los equipos disponibles y errores.

    public NotebookFragment() {
    }

    // Utilizado para mostrar el fragmento dinamicamente.
    public static NotebookFragment newInstance(int columnCount) {
        NotebookFragment fragment = new NotebookFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Agrega un menu de opciones a la Toolbar.

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    // Inicializa los contenidos del menu de opciones.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // Para evitar la repeticion de elementos al cambiar orientacion.
        inflater.inflate(R.menu.menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Hook invocado al seleccionar un item del menu de opciones.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Log.i(TAG, "onRefresh desde ActionBar");
                Toast.makeText(getContext(), R.string.toast_refresh, Toast.LENGTH_SHORT).show();
                requestJsonObject();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar vista (sin datos)
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        final Context context = view.getContext();

        // Inicializar fragmento.
        mBanner = (TextView) view.findViewById(R.id.informacion);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        // Obtener listado de items
        sedeAlumno = getArguments().getString("sede").toLowerCase();
        requestJsonObject();

        // Permite actualizar el listado deslizando hacia arriba.
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeOnRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh desde SwipeRefreshLayout");
                Toast.makeText(context, R.string.toast_refresh, Toast.LENGTH_SHORT).show();
                requestJsonObject();
                mSwipeRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NotebookListener) {
            mListener = (NotebookListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NotebookListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Funcion que permite realizar una consulta a el servidor de la biblioteca.
     * <p>
     * Tiene como objetivo realizar la REQUEST, la lectura del JSON, la serializacion a
     * un {@link List<Notebook>} y la asignación al adaptador del {@link RecyclerView}
     */
    private void requestJsonObject() {
        // Iniciar REQUEST
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = URL_BASE + sedeAlumno;
        Log.d(TAG, "Inicio de Query: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Respuesta: " + response);

                // Serializacion desde JSON
                JSONArray jsonArray;
                List<Notebook> notebooks = new ArrayList<Notebook>();
                Gson mGson = new Gson();
                try {
                    jsonArray = new JSONArray(response);
                    notebooks = Arrays.asList(mGson.fromJson(jsonArray.get(0).toString(), Notebook[].class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Ordenar notebooks por fecha de devolucion.
                Collections.sort(notebooks, new Comparator<Notebook>() {
                    @Override
                    public int compare(Notebook o1, Notebook o2) {
                        int estadoCompare = o1.getEstado().compareToIgnoreCase(o2.getEstado());
                        if (estadoCompare != 0) {
                            return estadoCompare;
                        } else {
                            int fechaCompare = o1.getFechaVencimiento().compareToIgnoreCase(o2.getFechaVencimiento());
                            if (fechaCompare != 0) {
                                return fechaCompare;
                            } else {
                                return -1 * o1.getHoraVencimiento().compareToIgnoreCase(o2.getHoraVencimiento());
                            }
                        }
                    }
                });
                Log.d(TAG, "Ordenado: " + notebooks.toString());

                // Define el adaptador del RecyclerView.
                mAdapter = new NotebookAdapter(getContext(), notebooks, mListener);
                mRecyclerView.setAdapter(mAdapter);

                // Actualizar TextView.
                actualizarContador();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error " + error.getMessage());
                mBanner.setText(R.string.volley_error_conexion);
            }
        });
        queue.add(stringRequest);
    }

    // Actualiza el TextView (contador) con información de los items disponibles.
    private void actualizarContador() {
        int total = mAdapter.getItemCount();
        int disponibles = mAdapter.getItemDisponibles();
        Log.d(TAG, "Disponibles: " + Integer.toString(disponibles) + " / " + Integer.toString(total));
        if (disponibles != 0) {
            mBanner.setText("Disponibles: " + Integer.toString(disponibles) + " / " + Integer.toString(total));
        } else {
            mBanner.setText("No hay notebooks disponibles.");
        }
    }

    /**
     * Esta interfaz tiene que ser implementada por las actividades que contengan este
     * fragmento, para permitir que la interacción realizada en este fragmento sea
     * comunidada a la actividad y otros fragmentos que esta pueda contener.
     */
    public interface NotebookListener {
        void onImageClicked(Notebook item);
    }
}
