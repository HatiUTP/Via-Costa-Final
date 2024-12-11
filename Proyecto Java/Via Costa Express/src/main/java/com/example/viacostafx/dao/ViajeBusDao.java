package com.example.viacostafx.dao;

import java.util.List;

import com.example.viacostafx.Modelo.ViajeBusModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * La clase ViajeBusDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los viajes de buses.
 */
public class ViajeBusDao {
    private final EntityManagerFactory emf;

    /**
     * Constructor que inicializa el EntityManagerFactory.
     */
    public ViajeBusDao() {
        emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    /**
     * Obtiene el ID del bus asociado a un viaje específico.
     *
     * @param viajeId El ID del viaje.
     * @return El ID del bus asociado al viaje, o -1 si no se encuentra.
     */
    public int obtenerBusIdPorViajeId(int viajeId) {
        EntityManager em = emf.createEntityManager();
        try {
            ViajeBusModel viajeBus = em.createQuery("SELECT vb FROM ViajeBusModel vb WHERE vb.viaje.id = :viajeId", ViajeBusModel.class)
                    .setParameter("viajeId", viajeId)
                    .getSingleResult();
            return viajeBus.getBus().getId();
        } catch (NoResultException e) {
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene una lista de ViajeBusModel por el ID del viaje.
     *
     * @param viajeId El ID del viaje.
     * @return Una lista de modelos de ViajeBusModel correspondientes al viaje proporcionado.
     */
    public List<ViajeBusModel> obtenerPorViajeId(int viajeId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ViajeBusModel> query = em.createQuery(
                    "SELECT vb FROM ViajeBusModel vb WHERE vb.viaje.id = :viajeId", ViajeBusModel.class);
            query.setParameter("viajeId", viajeId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
