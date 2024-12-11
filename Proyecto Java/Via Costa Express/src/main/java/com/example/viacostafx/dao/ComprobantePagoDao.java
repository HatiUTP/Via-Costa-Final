package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.ComprobantePagoModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * La clase ComprobantePagoDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los comprobantes de pago.
 */
public class ComprobantePagoDao {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("viacostaFX");

    /**
     * Registra un nuevo comprobante de pago en la base de datos.
     *
     * @param comprobantePago El modelo del comprobante de pago a registrar.
     */
    public void registrarComprobantePago(ComprobantePagoModel comprobantePago) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(comprobantePago);
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