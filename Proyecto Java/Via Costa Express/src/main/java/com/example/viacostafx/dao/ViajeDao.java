package com.example.viacostafx.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.viacostafx.Modelo.JPAUtils;
import com.example.viacostafx.Modelo.ViajeModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * La clase ViajeDao proporciona métodos para interactuar con la base de datos y gestionar la información relacionada con los viajes.
 */
public class ViajeDao {

    /**
     * Obtiene una lista de viajes disponibles según el origen, destino y fecha seleccionada.
     *
     * @param origenDistrito El distrito de origen.
     * @param destinoDistrito El distrito de destino.
     * @param fechaSeleccionada La fecha seleccionada para el viaje.
     * @return Una lista de modelos de viajes disponibles.
     */
    public static List<ViajeModel> obtenerViajesDisponibles(String origenDistrito, String destinoDistrito, LocalDate fechaSeleccionada) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<ViajeModel> viajes = null;

        try {
            // Obtener la fecha y hora actual
            LocalDateTime ahora = LocalDateTime.now();

            // Definir el inicio y fin del día seleccionado
            LocalDateTime inicioDia = fechaSeleccionada.atStartOfDay();
            LocalDateTime finDia = fechaSeleccionada.atTime(23, 59, 59);

            // Consulta JPQL para obtener los viajes disponibles
            String jpql = "SELECT DISTINCT v FROM ViajeModel v " +
                    "LEFT JOIN FETCH v.viajeBuses vb " +
                    "LEFT JOIN FETCH vb.bus b " +
                    "LEFT JOIN FETCH b.categoria c " +
                    "LEFT JOIN FETCH b.asientos a " +
                    "WHERE LOWER(v.agenciaOrigen.ubigeo.distrito) = LOWER(:origen) " +
                    "AND LOWER(v.agenciaDestino.ubigeo.distrito) = LOWER(:destino) " +
                    "AND v.fechaHoraSalida BETWEEN :inicioDia AND :finDia " +
                    "AND v.fechaHoraSalida >= :ahora";

            TypedQuery<ViajeModel> query = em.createQuery(jpql, ViajeModel.class);
            query.setParameter("origen", origenDistrito);
            query.setParameter("destino", destinoDistrito);
            query.setParameter("inicioDia", inicioDia);
            query.setParameter("finDia", finDia);
            query.setParameter("ahora", ahora);

            viajes = query.getResultList();
        } finally {
            em.close();
        }

        return viajes;
    }

    public ViajeModel obtenerViajePorId(int viajeId) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(ViajeModel.class, viajeId);
        } finally {
            em.close();
        }
    }

}
