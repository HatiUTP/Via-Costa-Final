package com.example.viacostafx.Modelo;

import java.time.LocalDateTime;
import java.util.List;

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

@Data
@Entity
@Table(name = "Boleto")
public class BoletoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Boleto_INT")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_Viaje")
    private ViajeModel viaje;

    @ManyToOne
    @JoinColumn(name = "ID_Asiento")
    private AsientoModel asiento;

    @Column(name = "fechaHoraCompra")
    private LocalDateTime fechaHoraCompra;

    @OneToMany(mappedBy = "boleto", fetch = FetchType.LAZY)
    private List<DetalleCompraModel> detalles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ViajeModel getViaje() {
        return viaje;
    }

    public void setViaje(ViajeModel viaje) {
        this.viaje = viaje;
    }

    public AsientoModel getAsiento() {
        return asiento;
    }

    public void setAsiento(AsientoModel asiento) {
        this.asiento = asiento;
    }

    public LocalDateTime getFechaHoraCompra() {
        return fechaHoraCompra;
    }

    public void setFechaHoraCompra(LocalDateTime fechaHoraCompra) {
        this.fechaHoraCompra = fechaHoraCompra;
    }

    public List<DetalleCompraModel> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraModel> detalles) {
        this.detalles = detalles;
    }
}
