package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.ClienteModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * La clase ClienteDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los clientes.
 */
public class ClienteDao {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("viacostaFX");

    /**
     * Obtiene un cliente por su DNI.
     *
     * @param dni El DNI del cliente.
     * @return El modelo del cliente correspondiente al DNI proporcionado, o null si no se encuentra.
     */
    public ClienteModel obtenerClientePorDNI(String dni) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ClienteModel> query = em.createQuery("SELECT c FROM ClienteModel c WHERE c.dni = :dni", ClienteModel.class);
            query.setParameter("dni", dni);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param id El ID del cliente.
     * @return El modelo del cliente correspondiente al ID proporcionado.
     */
    public ClienteModel obtenerClientePorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(ClienteModel.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Registra un nuevo cliente en la base de datos.
     *
     * @param cliente El modelo del cliente a registrar.
     * @return El ID del cliente registrado, o -1 si ocurre un error.
     */
    public int registrarCliente(ClienteModel cliente) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
            return cliente.getId(); // JPA automáticamente actualizará el ID después del persist
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