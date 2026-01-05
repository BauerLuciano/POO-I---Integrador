package com.eventos.repo;

import com.eventos.modelo.Persona;
import com.eventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class PersonaRepository {

    public void guardar(Persona p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Persona buscarPorDni(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL: Buscamos por el atributo 'dni' de la clase Persona
            return em.createQuery("SELECT p FROM Persona p WHERE p.dni = :dni", Persona.class)
                     .setParameter("dni", dni)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null; // No existe
        } finally {
            em.close();
        }
    }
}