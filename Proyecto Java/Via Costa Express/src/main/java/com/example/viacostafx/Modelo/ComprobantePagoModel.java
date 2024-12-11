package com.example.viacostafx.Modelo;
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

@Data
@Entity
@Table(name = "Comprobante_Pago")
public class ComprobantePagoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Comprobante_Pago")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "ID_Compra")
    private CompraModel compra;

    @Column(name = "fechaEmision")
    private LocalDateTime fechaEmision;
}
