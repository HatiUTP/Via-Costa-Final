package com.example.viacostafx.Modelo;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "Buses")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BusModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Bus_INT")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name="placa", length = 7)
    private String placa;

    @Column(name="modelo", length = 50)
    private String modelo;

    @Column(name="capacidad")
    private Integer capacidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Categoria")
    private CategoriaModel categoria;

    @Column(name="isActive")
    private Boolean isActive;

    @OneToMany(mappedBy = "bus")
    private List<ChoferModel> choferes;

    @OneToMany(mappedBy = "bus", fetch = FetchType.EAGER)
    private Set<AsientoModel> asientos;

    @OneToMany(mappedBy = "bus")
    private List<ViajeBusModel> viajeBuses;

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
