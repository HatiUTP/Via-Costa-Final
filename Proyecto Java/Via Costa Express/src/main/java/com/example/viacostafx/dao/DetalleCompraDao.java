package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.DetalleCompraModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * La clase DetalleCompraDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los detalles de las compras.
 */
public class DetalleCompraDao {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("viacostaFX");

    /**
     * Registra un nuevo detalle de compra en la base de datos.
     *
     * @param detalleCompra El modelo del detalle de compra a registrar.
     */
    public void registrarDetalleCompra(DetalleCompraModel detalleCompra) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(detalleCompra);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}