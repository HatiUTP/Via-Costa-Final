package com.example.viacostafx.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.viacostafx.Modelo.JPAUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * La clase AgenciaDao proporciona métodos para interactuar con la base de datos y obtener información relacionada con las agencias.
 */
@Service
public class AgenciaDao {
    
    /**
     * Obtiene una lista de todos los distritos que tienen agencias activas.
     */
    public static List<String> obtenerDistritosConAgencias() {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<String> distritos = null;

        try {
            String jpql = "SELECT DISTINCT a.ubigeo.distrito FROM AgenciaModel a WHERE a.isActive = true";
            TypedQuery<String> query = em.createQuery(jpql, String.class);
            distritos = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return distritos;
    }
}