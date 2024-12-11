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
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "Viaje")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ViajeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Viaje_INT")
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Agencia_Origen_INT")
    private AgenciaModel agenciaOrigen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Agencia_Destino_INT")
    private AgenciaModel agenciaDestino;

    private LocalDateTime fechaHoraSalida;

    private Boolean isActive;

    @OneToMany(mappedBy = "viaje", fetch = FetchType.EAGER)
    private List<ViajeBusModel> viajeBuses;

    @OneToMany(mappedBy = "viaje")
    private List<BoletoModel> boletos;
}
