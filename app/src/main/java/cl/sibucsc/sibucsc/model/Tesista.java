package cl.sibucsc.sibucsc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Sala que puede ser prestado a un Alumno.
 */
public class Tesista {

    @SerializedName("Sala")
    private String sala;
    @SerializedName("Fecha de inicio")
    private String fechaInicio;
    @SerializedName("Hora de inicio")
    private String horaInicio;
    @SerializedName("Hora de termino")
    private String horaTermino;
    @SerializedName("Estado")
    public String estado;

    public Tesista(String sala, String fechaInicio, String horaInicio, String horaTermino, String estado) {
        this.sala = sala;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
        this.estado = estado;
    }

    public String getSala() {
        return sala;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraTermino() {
        return horaTermino;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return getSala() + " " + getFechaInicio() + " " + getHoraInicio() + " " + getHoraTermino() + " " + getEstado();
    }
}
