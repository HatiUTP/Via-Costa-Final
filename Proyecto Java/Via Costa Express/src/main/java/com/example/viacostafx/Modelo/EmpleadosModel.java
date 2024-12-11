package com.example.viacostafx.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Empleados")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Empleado")
    private int id;

    @Column(name = "Nombre", length = 50)
    private String nombre;

    @Column(name = "Apellido", length = 50)
    private String apellido;

    @Column(name = "DNI", unique = true)
    private int DNI;

    @Column(name = "Telefono")
    private int telefono;

    @Column(name = "Usuario")
    private String usuario;

    @Column(name = "Contrasenia")
    private String contrasenia;
}