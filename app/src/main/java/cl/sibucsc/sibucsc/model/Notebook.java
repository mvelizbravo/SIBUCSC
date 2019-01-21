package cl.sibucsc.sibucsc.model;

import com.google.gson.annotations.SerializedName;

public class Notebook {

    @SerializedName("Copia")
    private String copia;
    @SerializedName("Codigo de Barras")
    private String codigoBarra;
    @SerializedName("Fecha de devolucion")
    private String fechaVencimiento;
    @SerializedName("Hora de devolucion")
    private String horaVencimiento;
    @SerializedName("Status")
    public String estado;

    public Notebook(String copia, String codigoBarra, String fechaVencimiento, String horaVencimiento, String estado) {
        this.copia = copia;
        this.codigoBarra = codigoBarra;
        this.fechaVencimiento = fechaVencimiento;
        this.horaVencimiento = horaVencimiento;
        this.estado = estado;
    }

    public String getCopia() {
        return copia;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getHoraVencimiento() {
        return horaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return getCopia() + " " + getCodigoBarra() + " " + getEstado() + " " + getFechaVencimiento() + " " + getHoraVencimiento();
    }
}
