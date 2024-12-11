package com.example.viacostafx.Modelo;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "Compra")
public class CompraModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Compra_INT")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "ID_Cliente")
    private ClienteModel cliente;

    @ManyToOne
    @JoinColumn(name = "ID_Empleado")
    private EmpleadosModel empleado;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<ComprobantePagoModel> comprobantes;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<DetalleCompraModel> detallesCompra;

    @Column(name = "precioTotal")
    private BigDecimal precioTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClienteModel getCliente() {
        return cliente;
    }

    public void setCliente(ClienteModel cliente) {
        this.cliente = cliente;
    }

    public EmpleadosModel getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadosModel empleado) {
        this.empleado = empleado;
    }

    public List<ComprobantePagoModel> getComprobantes() {
        return comprobantes;
    }

    public void setComprobantes(List<ComprobantePagoModel> comprobantes) {
        this.comprobantes = comprobantes;
    }

    public List<DetalleCompraModel> getDetallesCompra() {
        return detallesCompra;
    }

    public void setDetallesCompra(List<DetalleCompraModel> detallesCompra) {
        this.detallesCompra = detallesCompra;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }
}