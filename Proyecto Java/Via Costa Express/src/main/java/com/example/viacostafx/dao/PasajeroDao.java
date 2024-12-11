package com.example.viacostafx.dao;

import java.util.List;

import com.example.viacostafx.Modelo.PasajeroModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * La clase PasajeroDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los pasajeros.
 */
public class PasajeroDao {
    private final EntityManagerFactory emf;

    /**
     * Constructor que inicializa el EntityManagerFactory.
     */
    public PasajeroDao() {
        this.emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    /**
     * Registra un nuevo pasajero en la base de datos.
     *
     * @param pasajero El modelo del pasajero a registrar.
     * @return El ID del pasajero registrado, o -1 si ocurre un error.
     */
    public int registrarPasajero(PasajeroModel pasajero) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pasajero);
            em.getTransaction().commit();
            return pasajero.getId();
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

    /**
     * Actualiza un pasajero existente.
     *
     * @param pasajero El modelo del pasajero con la información actualizada.
     */
    public void actualizarPasajero(PasajeroModel pasajero) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(pasajero);
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

    /**
     * Obtiene una lista de todos los pasajeros.
     *
     * @return Una lista de modelos de pasajeros.
     */
    public List<PasajeroModel> obtenerTodosPasajeros() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PasajeroModel> query = em.createQuery("SELECT p FROM PasajeroModel p", PasajeroModel.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public PasajeroModel obtenerPasajeroPorDNI(String dni) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PasajeroModel> query = em.createQuery(
                "SELECT p FROM PasajeroModel p WHERE p.dni = :dni", 
                PasajeroModel.class);
            query.setParameter("dni", dni);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
