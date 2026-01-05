package com.eventos.repo;

import com.eventos.modelo.Evento;
import com.eventos.enums.EstadoEvento;
import com.eventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class EventoRepositoryImpl implements EventoRepository {

    @Override
    public void guardar(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin(); // Iniciamos transacción
            em.persist(evento);          // Guardamos
            em.getTransaction().commit(); // Confirmamos cambios
        } catch (Exception e) {
            em.getTransaction().rollback(); // Si falla, deshacemos
            throw e;
        } finally {
            em.close(); // Siempre cerramos el manager
        }
    }
    
    @Override
    public void actualizar(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(evento); // Merge actualiza un objeto existente
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Evento buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Evento.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL: Consultamos objetos, no tablas
            return em.createQuery("SELECT e FROM Evento e", Evento.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> buscarPorEstado(EstadoEvento estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Evento e WHERE e.estado = :estado", Evento.class)
                     .setParameter("estado", estado)
                     .getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                em.remove(evento);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}