package com.example.viacostafx.dao;

import java.util.List;

import com.example.viacostafx.Modelo.EmpleadosModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * La clase EmpleadosDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los empleados.
 */
public class EmpleadosDao {
    private final EntityManagerFactory emf;

    /**
     * Constructor que inicializa el EntityManagerFactory.
     */
    public EmpleadosDao() {
        this.emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    /**
     * Crea un nuevo empleado en la base de datos.
     *
     * @param empleado El modelo del empleado a crear.
     * @return true si el empleado fue creado correctamente, false en caso contrario.
     */
    public boolean crearEmpleado(EmpleadosModel empleado) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(empleado);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene una lista de todos los empleados.
     *
     * @return Una lista de modelos de empleados.
     */
    public List<EmpleadosModel> obtenerTodosEmpleados() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<EmpleadosModel> query = em.createQuery("SELECT e FROM EmpleadosModel e", EmpleadosModel.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // READ - Obtener empleado por ID
    public EmpleadosModel obtenerEmpleadoPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(EmpleadosModel.class, id);
        } finally {
            em.close();
        }
    }

    // Obtener empleado por nombre de usuario
    public EmpleadosModel obtenerEmpleadoPorUsername(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<EmpleadosModel> query = em.createQuery("SELECT e FROM EmpleadosModel e WHERE e.usuario = :username", EmpleadosModel.class);
            query.setParameter("username", username);
            return query.getSingleResult(); // Retorna un único resultado
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retorna null si hay otro error
        } finally {
            em.close();
        }
    }

    // UPDATE - Actualizar empleado
    public boolean actualizarEmpleado(EmpleadosModel empleado) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(empleado);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // DELETE - Eliminar empleado
    public boolean eliminarEmpleado(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            EmpleadosModel empleado = em.find(EmpleadosModel.class, id);
            if (empleado != null) {
                em.remove(empleado);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Verificar si un DNI ya existe en la base de datos
    public boolean existeDNI(int dni) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM EmpleadosModel e WHERE e.DNI = :dni", Long.class);
            query.setParameter("dni", dni);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Buscar empleados por nombre de usuario
    public List<EmpleadosModel> buscarEmpleadoPorUsuario(String usuario) {
        EntityManager em = emf.createEntityManager();
        List<EmpleadosModel> empleadosFiltrados = null;
    
        try {
            // Consulta con LIKE para encontrar empleados por usuario
            empleadosFiltrados = em.createQuery(
                "SELECT e FROM EmpleadosModel e WHERE e.usuario LIKE :usuario", EmpleadosModel.class)
                .setParameter("usuario", "%" + usuario + "%")
                .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    
        return empleadosFiltrados;
    }
}
