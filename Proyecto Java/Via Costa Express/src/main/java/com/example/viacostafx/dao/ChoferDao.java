package com.example.viacostafx.dao;

import java.util.List;

import com.example.viacostafx.Modelo.ChoferModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * La clase ChoferDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los choferes.
 */
public class ChoferDao {
    private final EntityManagerFactory emf;

    /**
     * Constructor que inicializa el EntityManagerFactory.
     */
    public ChoferDao() {
        emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    public List<ChoferModel> obtenerTodosLosChoferes() {
        EntityManager em = emf.createEntityManager();
        List<ChoferModel> choferesActivos = null;

        try {
            // Consulta para traer solo los choferes con isActive = true
            choferesActivos = em.createQuery("SELECT c FROM ChoferModel c WHERE c.isActive = true", ChoferModel.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return choferesActivos;
    }


    // Metodo para agregar un nuevo chofer
    public void agregarChofer(ChoferModel chofer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(chofer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    //Metodo para editar un chofer
    public void editarChofer(ChoferModel chofer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(chofer); // Actualiza el chofer en la base de datos
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

    //Metodo pra eliminar un chofer
    public void eliminarChofer(int choferId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ChoferModel chofer = em.find(ChoferModel.class, choferId); // Busca el chofer por su ID
            if (chofer != null) {
                em.remove(chofer); // Elimina el chofer de la base de datos
            }
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
     * Desactiva un chofer cambiando su estado a inactivo.
     *
     * @param idChofer El ID del chofer a desactivar.
     */
    public void desactivarChofer(int idChofer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Encontrar el chofer por su ID
            ChoferModel chofer = em.find(ChoferModel.class, idChofer);

            if (chofer != null) {
                // Cambiar isActive a false
                chofer.setIsActive(false);
                em.merge(chofer);  // Actualizar el chofer en la base de datos
            }

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
     * Busca choferes por nombre.
     *
     * @param nombre El nombre del chofer a buscar.
     * @return Una lista de modelos de choferes que coinciden con el nombre proporcionado.
     */
    public List<ChoferModel> buscarChoferPorNombre(String nombre) {
        EntityManager em = emf.createEntityManager();
        List<ChoferModel> choferesFiltrados = null;

        try {
            // Consulta con LIKE para encontrar choferes por nombre
            choferesFiltrados = em.createQuery("SELECT c FROM ChoferModel c WHERE c.isActive = true AND c.nombre LIKE :nombre", ChoferModel.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return choferesFiltrados;
    }
}
