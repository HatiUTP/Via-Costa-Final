package com.example.viacostafx.Auxiliar;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * La clase PasajeroData se utiliza para representar los datos de un pasajero en la interfaz de usuario.
 * Proporciona propiedades observables para facilitar el enlace de datos con los componentes de la interfaz de usuario.
 */
public class PasajeroData {
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty dni;
    private final SimpleStringProperty telefono;
    private final SimpleStringProperty email;
    private final SimpleStringProperty asiento;
    private final double precio;
    private final int asientoId;

    /**
     * Constructor que inicializa los datos del pasajero.
     *
     * @param nombre El nombre del pasajero.
     * @param dni El DNI del pasajero.
     * @param telefono El teléfono del pasajero.
     * @param email El correo electrónico del pasajero.
     * @param asiento El asiento asignado al pasajero.
     * @param precio El precio del boleto del pasajero.
     * @param asientoId El ID del asiento asignado al pasajero.
     */
    public PasajeroData(String nombre, String dni, String telefono, String email, String asiento, double precio, int asientoId) {
        this.nombre = new SimpleStringProperty(nombre);
        this.dni = new SimpleStringProperty(dni);
        this.telefono = new SimpleStringProperty(telefono);
        this.email = new SimpleStringProperty(email);
        this.asiento = new SimpleStringProperty(asiento);
        this.precio = precio;
        this.asientoId = asientoId;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty dniProperty() {
        return dni;
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty asientoProperty() {
        return asiento;
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getDni() {
        return dni.get();
    }

    public String getTelefono() {
        return telefono.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getAsiento() {
        return asiento.get();
    }

    public double getPrecio() {
        return precio;
    }

    public int getAsientoId() {
        return asientoId;
    }
}