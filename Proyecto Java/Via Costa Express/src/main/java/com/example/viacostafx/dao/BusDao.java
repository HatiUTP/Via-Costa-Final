package com.example.viacostafx.dao;

import java.util.List;

import com.example.viacostafx.Modelo.BusModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * La clase BusDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los buses.
 */
public class BusDao {
    private final EntityManagerFactory emf;

    /**
     * Constructor que inicializa el EntityManagerFactory.
     */
    public BusDao() {
        emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    /**
     * Obtiene un bus por su ID.
     *
     * @param busId El ID del bus.
     * @return El modelo del bus correspondiente al ID proporcionado.
     */
    public BusModel obtenerBusPorId(int busId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(BusModel.class, busId);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene una lista de todos los buses.
     *
     * @return Una lista de modelos de buses.
     */
    public List<BusModel> obtenerTodosLosBuses() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("from BusModel", BusModel.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return null;
    }
    
    // Obtiene un bus con sus choferes por su ID.
    public BusModel obtenerBusConChoferesPorId(Integer busId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT b FROM BusModel b " +
                "LEFT JOIN FETCH b.choferes " +
                "WHERE b.id = :busId", BusModel.class)
                .setParameter("busId", busId)
                .getSingleResult();
        } finally {
            em.close();
        }
    }

}
