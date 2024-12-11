package com.example.viacostafx.Auxiliar;

/**
 * La clase Session permite almacenar y recuperar la ID del empleado que ha iniciado sesión.
 */

public class Session {
    // Instancia única de la clase Session
    private static Session instance;
    // ID del empleado que ha iniciado sesión
    private int empleadoId;

    // Constructor privado para evitar la creación de instancias adicionales
    private Session() {}

    /**
     * Obtiene la instancia única de la clase Session.
     * Si no existe una instancia, se crea una nueva.
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }
}
