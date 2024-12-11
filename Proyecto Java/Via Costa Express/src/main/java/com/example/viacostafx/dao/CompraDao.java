package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.CompraModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * La clase CompraDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con las compras.
 */
public class CompraDao {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("viacostaFX");

    /**
     * Registra una nueva compra en la base de datos.
     *
     * @param compra El modelo de la compra a registrar.
     * @return El ID de la compra registrada, o -1 si ocurre un error.
     */
    public int registrarCompra(CompraModel compra) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(compra);
            em.getTransaction().commit();
            return compra.getId(); // JPA automáticamente actualizará el ID después del persist
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return -1;
        } finally {
            em.close();
        }
    }
}