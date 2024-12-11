package com.example.viacostafx.Modelo;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Entity
@Table(name = "Pasajero")
@Data
public class PasajeroModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Pasajero")
    private Integer id;

    @Column(name="Nombre", length = 50)
    private String nombre;

    @Column(name="DNI", length = 8)
    private String dni;

    @Column(name="Email", length = 100)
    private String email;

    @Column(name="Telefono", length = 15)
    private String telefono;

    @OneToMany(mappedBy = "pasajero", cascade = CascadeType.ALL)
    private List<DetalleCompraModel> detallesCompra;

    // JavaFX Properties
    public IntegerProperty idProperty() {
        return new SimpleIntegerProperty(id);
    }

    public StringProperty nombreProperty() {
        return new SimpleStringProperty(nombre);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<DetalleCompraModel> getDetallesCompra() {
        return detallesCompra;
    }

    public void setDetallesCompra(List<DetalleCompraModel> detallesCompra) {
        this.detallesCompra = detallesCompra;
    }

    public StringProperty dniProperty() {
        return new SimpleStringProperty(dni);
    }

    public StringProperty emailProperty() {
        return new SimpleStringProperty(email);
    }

    public StringProperty telefonoProperty() {
        return new SimpleStringProperty(telefono);
    }
}