package com.example.viacostafx.dao;

import java.util.List;

import com.example.viacostafx.Modelo.AsientoModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * La clase AsientoDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los asientos.
 */
public class AsientoDao {
    private final EntityManagerFactory emf;

    /**
     * Constructor que inicializa el EntityManagerFactory.
     */
    public AsientoDao() {
        this.emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    /**
     * Obtiene un asiento por su ID.
     *
     * @param id El ID del asiento.
     * @return El modelo del asiento correspondiente al ID proporcionado.
     */
    public AsientoModel obtenerAsientoPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(AsientoModel.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene una lista de asientos por el ID del bus.
     *
     * @param busId El ID del bus.
     * @return Una lista de modelos de asientos correspondientes al bus proporcionado.
     */
    public List<AsientoModel> obtenerAsientosPorBus(int busId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<AsientoModel> query = em.createQuery("SELECT a FROM AsientoModel a WHERE a.bus.id = :busId", AsientoModel.class);
            query.setParameter("busId", busId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza un asiento existente.
     *
     * @param asientoActualizado El modelo del asiento con la información actualizada.
     * @return true si el asiento fue actualizado correctamente, false en caso contrario.
     */
    public boolean actualizarAsiento(AsientoModel asientoActualizado) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AsientoModel asiento = em.find(AsientoModel.class, asientoActualizado.getId());
            if (asiento != null) {
                asiento.setEstado(asientoActualizado.getEstado());
                em.merge(asiento);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}
