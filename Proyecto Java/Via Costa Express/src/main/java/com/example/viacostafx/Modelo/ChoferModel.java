package com.example.viacostafx.Modelo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name = "Choferes")
public class ChoferModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Chofer_INT")
    private Integer id;

    @Column(name="nombre", length = 50)
    private String nombre;

    @Column(name="dni", length = 8)
    private String dni;

    @Column(name = "Nro_Licencia", length = 20)
    private String numeroLicencia;

    @Column(name="telefono", length = 15)
    private String telefono;

    @Column(name="email", length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Bus")
    private BusModel bus;

    @Column(name="isActive")
    private Boolean isActive;
}
