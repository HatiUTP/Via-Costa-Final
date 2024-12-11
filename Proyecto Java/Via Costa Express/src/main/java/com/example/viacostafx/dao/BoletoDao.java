package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.BoletoModel;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * La clase BoletoDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los boletos.
 */
public class BoletoDao {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("viacostaFX");

    /**
     * Registra un nuevo boleto en la base de datos.
     *
     * @param boleto El modelo del boleto a registrar.
     */
    public void registrarBoleto(BoletoModel boleto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(boleto);
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
     * Obtiene una lista de boletos por el ID del viaje.
     *
     * @param viajeId El ID del viaje.
     * @return Una lista de modelos de boletos correspondientes al viaje proporcionado.
     */
    public List<BoletoModel> obtenerBoletosPorViajeId(int viajeId) {
        EntityManager em = emf.createEntityManager();
        try {
            // Usar join fetch para cargar los detalles eagerly
            String jpql = "SELECT DISTINCT b FROM BoletoModel b " +
                         "LEFT JOIN FETCH b.detalles d " +
                         "LEFT JOIN FETCH d.pasajero " +
                         "LEFT JOIN FETCH b.asiento " +
                         "WHERE b.viaje.id = :viajeId";
            
            TypedQuery<BoletoModel> query = em.createQuery(jpql, BoletoModel.class);
            query.setParameter("viajeId", viajeId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}