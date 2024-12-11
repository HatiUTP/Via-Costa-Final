package com.example.viacostafx.Modelo;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Categoria")
public class CategoriaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Categoria_INT")
    private Integer id;

    @Column(name="nombre", length = 50)
    private String nombre;

    @Column(name="descripcion", length = 255)
    private String descripcion;

    @Column(name = "Costo_extra")
    private BigDecimal costoExtra;

    @Column(name = "isActive")
    private Boolean isActive;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.EAGER)
    private List<BusModel> buses;

}
