package cl.sibucsc.sibucsc.model;

import com.google.gson.annotations.SerializedName;

public class Alumno {

    @SerializedName("Nombre")
    private String nombre;
    @SerializedName("RUT")
    private String rut;
    @SerializedName("Carrera")
    private String carrera;
    @SerializedName("Biblioteca")
    private String sede;
    @SerializedName("Estado")
    private String estado;
    @SerializedName("Sancion")
    private String sancion;

    public Alumno(String nombre, String rut, String carrera, String sede, String estado, String sancion) {
        this.nombre = nombre;
        this.rut = rut;
        this.carrera = carrera;
        this.sede = sede;
        this.estado = estado;
        this.sancion = sancion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

    public String getCarrera() {
        return carrera;
    }

    public String getSede() {
        return sede;
    }

    public String getEstado() {
        return estado;
    }

    public String getSancion() {
        return sancion;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    @Override
    public String toString() {
        return getNombre() + " " + getRut() + " " + getCarrera() + " " + getSede() + " " + getEstado() + " " + getSancion();
    }
}