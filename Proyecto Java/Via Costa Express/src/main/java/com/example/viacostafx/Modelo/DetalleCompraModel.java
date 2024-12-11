package com.example.viacostafx.Modelo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Detalle_Compra")
@Data
public class DetalleCompraModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Detalle_INT")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "ID_Compra")
    private CompraModel compra;
    
    @ManyToOne
    @JoinColumn(name = "ID_Pasajero")
    private PasajeroModel pasajero;
    
    @ManyToOne
    @JoinColumn(name = "ID_Boleto")
    private BoletoModel boleto;

    @Column(name = "fechaHoraCompra")
    private LocalDateTime fechaHoraCompra;

    @Column(name = "precio")
    private BigDecimal precio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CompraModel getCompra() {
        return compra;
    }

    public void setCompra(CompraModel compra) {
        this.compra = compra;
    }

    public PasajeroModel getPasajero() {
        return pasajero;
    }

    public void setPasajero(PasajeroModel pasajero) {
        this.pasajero = pasajero;
    }

    public BoletoModel getBoleto() {
        return boleto;
    }

    public void setBoleto(BoletoModel boleto) {
        this.boleto = boleto;
    }

    public LocalDateTime getFechaHoraCompra() {
        return fechaHoraCompra;
    }

    public void setFechaHoraCompra(LocalDateTime fechaHoraCompra) {
        this.fechaHoraCompra = fechaHoraCompra;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}