package cl.sibucsc.sibucsc.recyclerview;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cl.sibucsc.sibucsc.R;
import cl.sibucsc.sibucsc.common.DatePickerFragment;
import cl.sibucsc.sibucsc.model.Tesista;
import cl.sibucsc.sibucsc.recyclerview.decoration.SimpleDividerItemDecoration;

/**
 * {@link Fragment} tiene la funcion de mostrar un listado de {@link Tesista} y
 * cuantificar cuantos de estos tienen estado 'Disponible'.
 * <p>
 * Las actividades que contengan este fragmento deben implementar la
 * interfaz {@link TesistaListener}.
 */
public class TesistaFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TesistaPrestamo";

    // Numero de columnas y valor por defecto
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    // Plantilla
    private RecyclerView mRecyclerView; // Muestra varias vistas de un mismo tipo.
    private TesistaAdapter mAdapter; // Se encarga de asignar los datos y la vista al RV.
    private TesistaListener mListener; // Permite realizar acciones establecidas en la interfaz.
    private SwipeRefreshLayout mSwipeRefresh; // Se utiliza para actualizar los datos.
    private TextView mBanner; // Para visualizar las salas de tesistas.

    // Selector
    private Spinner mSala; // Selector de sala
    private ArrayAdapter<CharSequence> mArrayAdapter; // Asigna un arreglo a el spinner
    private Button mFecha; // Boton que invoca el calendario
    private String a; // Auxiliar
    private String b; // Auxiliar

    // Listener que actua cuando se ha seleccionado una fecha en el calendario.
    private DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            String aux = DateFormat.getDateInstance().format(calendar.getTime());
            mFecha.setText(aux);
            b = fechaURL(year, month, day);
            Log.i(TAG, "Fecha: " + b);
            requestJsonObject();
        }
    };

    public TesistaFragment() {
    }

    // Utilizado para mostrar el fragmento dinamicamente.
    public static TesistaFragment newInstance(int columnCount) {
        TesistaFragment fragment = new TesistaFragment();
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
        View view = inflater.inflate(R.layout.fragment_tesistas, container, false);
        final Context context = view.getContext();

        // Inicializar fragmento.
        mSala = (Spinner) view.findViewById(R.id.spinnerSala);
        mFecha = (Button) view.findViewById(R.id.btnFecha);
        mBanner = (TextView) view.findViewById(R.id.informacion);
        mBanner.setText(R.string.tes_seleccion);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        // Funcionalidad spinner sala.
        mArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.tesistas_array, R.layout.layout_spinner_item);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSala.setAdapter(mArrayAdapter);
        mSala.setOnItemSelectedListener(this);
        a = "1";

        // Funcionalidad boton fecha.
        mFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment().newInstance();
                fragment.setCallBack(onDate);
                fragment.show(getFragmentManager().beginTransaction(), "DatePickerFragment");
            }
        });
        Calendar now = Calendar.getInstance();
        mFecha.setText(DateFormat.getDateInstance().format(now.getTime()));
        b = fechaURL(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

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

    // Listener para el spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sala = parent.getItemAtPosition(position).toString();
        a = sala.substring(sala.length() - 1);
        Log.i(TAG, "Numero: " + a);
        requestJsonObject();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TesistaListener) {
            mListener = (TesistaListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TesistaListener");
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
     * un {@link List<Tesista>} y la asignación al adaptador del {@link RecyclerView}
     */
    private void requestJsonObject() {
        // Iniciar REQUEST
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = obtenerURL(a, b);
        Log.d(TAG, "Inicio de Query: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Respuesta: " + response);
                mBanner.setText(R.string.tes_seleccion);

                // Serializacion desde JSON
                JSONArray jsonArray;
                List<Tesista> salas = new ArrayList<Tesista>();
                Gson mGson = new Gson();

                try {
                    jsonArray = new JSONArray(response);
                    salas = Arrays.asList(mGson.fromJson(jsonArray.get(0).toString(), Tesista[].class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Salas: " + salas.toString());

                // Selecciona una fecha de fin de semana
                if (response.equals("[[{\"Estado\": \"No se encontraron bloques para la fecha especificada\"}]]")) {
                    Log.d(TAG, "Se ha seleccionado un fin de semana.");
                    mBanner.setText(R.string.tes_error_fds);
                    salas = new ArrayList<>();
                    salas.add(new Tesista("", "", "", "", ""));
                }

                // Define el adaptador del RecyclerView.
                mAdapter = new TesistaAdapter(getContext(), salas, mListener);
                mRecyclerView.setAdapter(mAdapter);
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

    // Generar URL para la Query
    private String obtenerURL(String a, String b) {
        return getString(R.string.tes_url) + "/" + a + "/" + b + "/";
    }

    // Obtener fecha en formato valido para la URL
    private String fechaURL(int year, int month, int day) {
        month++;
        return Integer.toString(year) + String.format("%02d", month) + String.format("%02d", day);
    }

    // Abre el calendario para seleccionar una fecha.
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Esta interfaz tiene que ser implementada por las actividades que contengan este
     * fragmento, para permitir que la interacción realizada en este fragmento sea
     * comunidada a la actividad y otros fragmentos que esta pueda contener.
     */
    public interface TesistaListener {
        void onImageClicked(Tesista item);
    }
}